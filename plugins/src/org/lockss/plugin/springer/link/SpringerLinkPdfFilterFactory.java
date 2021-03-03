/*

Copyright (c) 2000-2021, Board of Trustees of Leland Stanford Jr. University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.plugin.springer.link;

import java.io.*;

import org.lockss.filter.pdf.*;
import org.lockss.filter.pdf.PageTransformUtil.ExtractStringsToOutputStream;
import org.lockss.plugin.*;
import org.lockss.util.*;
import org.pdfbox.cos.*;
import org.pdfbox.pdmodel.*;

public class SpringerLinkPdfFilterFactory implements FilterFactory {

  /**
   * <p>Bypasses {@link TextScrapingDocumentTransform}, which fails on
   * zero-length results. To be fixed in the PDF framework later.</p>
   * @see TextScrapingDocumentTransform
   */
  public class ResilientTextScrapingDocumentTransform extends OutputStreamDocumentTransform {
	    public DocumentTransform makePreliminaryTransform() throws IOException {
	        return new NormalizeMetadata();
	      }
    public DocumentTransform makeTransform() throws IOException {
      return new ConditionalDocumentTransform(makePreliminaryTransform(),
                                              false, // Difference with TextScrapingDocumentTransform
                                              new TransformEachPage(new ExtractStringsToOutputStream(outputStream) {
                                                @Override
                                                public void processStream(PDPage arg0, PDResources arg1, COSStream arg2) throws IOException {
                                                  logger.debug3("ResilientTextScrapingDocumentTransform: unconditional signalChange()");
                                                  signalChange(); // Difference with TextScrapingDocumentTransform
                                                  super.processStream(arg0, arg1, arg2);
                                                }
                                              }));
    }
  }


  public static class EraseMetadataSection implements DocumentTransform {

    public boolean transform(PdfDocument pdfDocument) throws IOException {
      pdfDocument.setMetadata(" ");
      return true;
    }

  }


  public static class NormalizeMetadata extends AggregateDocumentTransform {

    public NormalizeMetadata() {
      super(// Remove the modification date
            new RemoveModificationDate(),
            // Remove the text in the metadata section
            new EraseMetadataSection(),
            // Remove the variable part of the document ID
            new NormalizeTrailerId());
    }

  }

  public static class NormalizeTrailerId implements DocumentTransform {

    public boolean transform(PdfDocument pdfDocument) throws IOException {
      COSDictionary trailer = pdfDocument.getTrailer();
      if (trailer != null) {
        // Put bogus ID to prevent autogenerated (variable) ID
        COSArray id = new COSArray();
        id.add(new COSString("12345678901234567890123456789012"));
        id.add(id.get(0));
        trailer.setItem(COSName.getPDFName("ID"), id);
        return true; // success
      }
      return false; // all other cases are unexpected
    }

  }

  public static class RemoveModificationDate implements DocumentTransform {

    public boolean transform(PdfDocument pdfDocument) throws IOException {
      pdfDocument.removeModificationDate();
      return true;
    }

  }

  public InputStream createFilteredInputStream(ArchivalUnit au,
                                               InputStream in,
                                               String encoding) {
    logger.debug2("PDF filter factory for: " + au.getName());
    OutputDocumentTransform documentTransform = new ResilientTextScrapingDocumentTransform();
    if (documentTransform == null) {
      logger.debug2("Unfiltered");
      return in;
    }
    else {
      return PdfUtil.applyFromInputStream(documentTransform, in);
    }
  }

  /**
   * <p>A logger for use by this class.</p>
   */
  private static Logger logger = Logger.getLogger(SpringerLinkPdfFilterFactory.class);

}
