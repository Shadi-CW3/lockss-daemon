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

package org.lockss.filter.pdf;

import java.io.IOException;
import java.util.*;

import org.lockss.util.*;
import org.lockss.util.PdfUtil.ResultPolicy;

/**
 * <p>A page transform made of many other page transforms,
 * applied sequentially.
 * @author Thib Guicherd-Callin
 * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
 */
@Deprecated
public class AggregatePageTransform implements PageTransform {

  /**
   * <p>A list of registered {@link PageTransform} instances.</p>
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  protected List /* of PageTransform */ pageTransforms;

  /**
   * <p>A result policy determining the boolean result of the
   * transform.</p>
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  protected ResultPolicy resultPolicy;

  /**
   * <p>Builds a new aggregate page transform using the default
   * result policy.</p>
   * @see #AggregatePageTransform(PdfUtil.ResultPolicy)
   * @see #POLICY_DEFAULT
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public AggregatePageTransform() {
    this(POLICY_DEFAULT);
  }

  /**
   * <p>Builds a new aggregate page transform using the default
   * result policy and registers the given page transforms.</p>
   * @param pageTransform A page transform.
   * @see #AggregatePageTransform(PdfUtil.ResultPolicy, PageTransform)
   * @see #POLICY_DEFAULT
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public AggregatePageTransform(PageTransform pageTransform) {
    this(POLICY_DEFAULT,
         pageTransform);
  }

  /**
   * <p>Builds a new aggregate page transform using the default
   * result policy and registers the given page transforms.</p>
   * @param pageTransform1 A page transform.
   * @param pageTransform2 A page transform.
   * @see #AggregatePageTransform(PdfUtil.ResultPolicy, PageTransform, PageTransform)
   * @see #POLICY_DEFAULT
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public AggregatePageTransform(PageTransform pageTransform1,
                                PageTransform pageTransform2) {
    this(POLICY_DEFAULT,
         pageTransform1,
         pageTransform2);
  }

  /**
   * <p>Builds a new aggregate page transform using the default
   * result policy and registers the given page transforms.</p>
   * @param pageTransform1 A page transform.
   * @param pageTransform2 A page transform.
   * @param pageTransform3 A page transform.
   * @see #AggregatePageTransform(PdfUtil.ResultPolicy, PageTransform, PageTransform, PageTransform)
   * @see #POLICY_DEFAULT
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public AggregatePageTransform(PageTransform pageTransform1,
                                PageTransform pageTransform2,
                                PageTransform pageTransform3) {
    this(POLICY_DEFAULT,
         pageTransform1,
         pageTransform2,
         pageTransform3);
  }

  /**
   * <p>Builds a new aggregate transform using the default result
   * policy and registers the given page transforms.</p>
   * @param pageTransforms     An array of page transforms.
   * @see #AggregatePageTransform(PdfUtil.ResultPolicy, PageTransform[])
   * @see #POLICY_DEFAULT
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public AggregatePageTransform(PageTransform[] pageTransforms) {
    this(POLICY_DEFAULT,
         pageTransforms);
  }

  /**
   * <p>Builds a new aggregate page transform using the given
   * result policy.</p>
   * @param resultPolicy   A result policy.
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public AggregatePageTransform(ResultPolicy resultPolicy) {
    if (resultPolicy == null) {
      String logMessage = "Cannot specify a null result policy";
      logger.error(logMessage);
      throw new NullPointerException(logMessage);
    }
    logger.debug3("Setting up result policy " + resultPolicy.toString());
    this.resultPolicy = resultPolicy;
    this.pageTransforms = new ArrayList();
  }

  /**
   * <p>Builds a new aggregate page transform using the given
   * result policy and registers the given page transforms.</p>
   * @param resultPolicy   A result policy.
   * @param pageTransform A page transform.
   * @see #AggregatePageTransform(PdfUtil.ResultPolicy)
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public AggregatePageTransform(ResultPolicy resultPolicy,
                                PageTransform pageTransform) {
    this(resultPolicy);
    logger.debug3("Setting up first page transform");
    add(pageTransform);
  }

  /**
   * <p>Builds a new aggregate page transform using the given
   * result policy and registers the given page transforms.</p>
   * @param resultPolicy   A result policy.
   * @param pageTransform1 A page transform.
   * @param pageTransform2 A page transform.
   * @see #AggregatePageTransform(PdfUtil.ResultPolicy, PageTransform)
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public AggregatePageTransform(ResultPolicy resultPolicy,
                                PageTransform pageTransform1,
                                PageTransform pageTransform2) {
    this(resultPolicy,
         pageTransform1);
    logger.debug3("Setting up second page transform");
    add(pageTransform2);
  }

  /**
   * <p>Builds a new aggregate page transform using the given
   * result policy and registers the given page transforms.</p>
   * @param resultPolicy   A result policy.
   * @param pageTransform1 A page transform.
   * @param pageTransform2 A page transform.
   * @param pageTransform3 A page transform.
   * @see #AggregatePageTransform(PdfUtil.ResultPolicy, PageTransform, PageTransform)
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public AggregatePageTransform(ResultPolicy resultPolicy,
                                PageTransform pageTransform1,
                                PageTransform pageTransform2,
                                PageTransform pageTransform3) {
    this(resultPolicy,
         pageTransform1,
         pageTransform2);
    logger.debug3("Setting up third page transform");
    add(pageTransform3);
  }

  /**
   * <p>Builds a new aggregate transform using the given result
   * policy and registers the given page transforms.</p>
   * @param resultPolicy       A result policy.
   * @param pageTransforms     An array of page transforms.
   * @see #AggregatePageTransform(PdfUtil.ResultPolicy)
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public AggregatePageTransform(ResultPolicy resultPolicy,
                                PageTransform[] pageTransforms) {
    this(resultPolicy);
    logger.debug3("Setting up " + pageTransforms.length + " page transforms");
    add(pageTransforms);
  }

  /**
   * <p>Registers a new {@link PageTransform} instance with
   * this aggregate page transform.</p>
   * <p>When transforming a PDF page, the actions performed by the
   * registered page tranforms are applied in the order the page
   * transforms were registered with this method.</p>
   * @param pageTransform A {@link PageTransform} instance.
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public synchronized void add(PageTransform pageTransform) {
    if (pageTransform == null) {
      String logMessage = "Cannot add a null page transform";
      logger.error(logMessage);
      throw new NullPointerException(logMessage);
    }
    logger.debug3("Adding a page transform");
    pageTransforms.add(pageTransform);
  }

  /**
   * <p>Registers new {@link DocumentTransform} instances with
   * this aggregate document transform.</p>
   * <p>When transforming a PDF document, the actions performed by the
   * registered tranforms are applied in the order the transforms
   * were registered with this method. This method registers the
   * document transforms in the array in array order.</p>
   * @param pageTransforms An array of {@link PageTransform} instances.
   * @see #add(PageTransform)
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public void add(PageTransform[] pageTransforms) {
    if (pageTransforms == null) {
      String logMessage = "Cannot add a null array of page transforms";
      logger.error(logMessage);
      throw new NullPointerException(logMessage);
    }
    for (int tra = 0 ; tra < pageTransforms.length ; ++tra) {
      logger.debug3("Adding page transform at index " + tra);
      add(pageTransforms[tra]);
    }
  }

  /* Inherit documentation */
  @Deprecated
  public boolean transform(PdfPage pdfPage) throws IOException {
    logger.debug2("Begin aggregate page transform with result policy " + resultPolicy.toString());
    boolean success = resultPolicy.initialValue();
    logger.debug3("Aggregate success flag initially " + success);
    for (Iterator iter = pageTransforms.iterator() ; iter.hasNext() ; ) {
      PageTransform pageTransform = (PageTransform)iter.next();
      success = resultPolicy.updateResult(success, pageTransform.transform(pdfPage));
      logger.debug3("Aggregate success flag now " + success);
      if (!resultPolicy.shouldKeepGoing(success)) {
        logger.debug3("Aggregation should not keep going");
        break;
      }
    }
    logger.debug2("Aggregate page transform result: " + success);
    return success;
  }

  /**
   * <p>The default result policy used by this class.</p>
   * @see #AggregatePageTransform()
   * @see #AggregatePageTransform(PageTransform)
   * @see #AggregatePageTransform(PageTransform, PageTransform)
   * @see #AggregatePageTransform(PageTransform, PageTransform, PageTransform)
   * @see #AggregatePageTransform(PageTransform[])
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  public static final ResultPolicy POLICY_DEFAULT = PdfUtil.AND;

  /**
   * <p>A logger for use by this class.</p>
   * @deprecated Moving away from PDFBox 0.7.3 after 1.76.
   */
  @Deprecated
  private static Logger logger = Logger.getLogger(AggregatePageTransform.class);

}
