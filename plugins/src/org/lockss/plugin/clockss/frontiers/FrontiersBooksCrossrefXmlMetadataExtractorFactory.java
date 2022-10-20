/*
 * $Id:$
 */

/*

 Copyright (c) 2000-2016 Board of Trustees of Leland Stanford Jr. University,
 all rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Except as contained in this notice, the name of Stanford University shall not
 be used in advertising or otherwise to promote the sale, use or other dealings
 in this Software without prior written authorization from Stanford University.

 */

package org.lockss.plugin.clockss.frontiers;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.lockss.daemon.PluginException;
import org.lockss.extractor.ArticleMetadata;
import org.lockss.extractor.FileMetadataExtractor;
import org.lockss.extractor.MetadataField;
import org.lockss.extractor.MetadataTarget;
import org.lockss.plugin.CachedUrl;
import org.lockss.plugin.clockss.CrossRefSchemaHelper;
import org.lockss.plugin.clockss.SourceXmlMetadataExtractorFactory;
import org.lockss.plugin.clockss.SourceXmlSchemaHelper;
import org.lockss.util.Logger;

import java.util.ArrayList;
import java.util.List;


public class FrontiersBooksCrossrefXmlMetadataExtractorFactory extends SourceXmlMetadataExtractorFactory {
  private static final Logger log = Logger.getLogger(FrontiersBooksCrossrefXmlMetadataExtractorFactory.class);

  private static SourceXmlSchemaHelper publishingHelper = null;
  private static final String PUBLISHER = "Frontiers Books";

  @Override
  public FileMetadataExtractor createFileMetadataExtractor(MetadataTarget target,
      String contentType)
          throws PluginException {
    return new AimsCRXmlMetadataExtractor();
  }

  public class AimsCRXmlMetadataExtractor extends SourceXmlMetadataExtractor {

    @Override
    protected SourceXmlSchemaHelper setUpSchema(CachedUrl cu) {
      // Once you have it, just keep returning the same one. It won't change.
      if (publishingHelper == null) {
          publishingHelper = new CrossRefSchemaHelper();
      }
      return publishingHelper;
    }
    
    /*
    @Override
    protected List<String> getFilenamesAssociatedWithRecord(SourceXmlSchemaHelper helper, CachedUrl cu,
        ArticleMetadata oneAM) {

      String cuBase = FilenameUtils.getFullPath(cu.getUrl());
      String doiValue = oneAM.getRaw(helper.getFilenameXPathKey());
      String doiSecond = StringUtils.substringAfter(doiValue, "/");
      String doiFirst = StringUtils.substringBefore(doiValue, "/");
      //2017 options
      String pdfName = cuBase + doiValue + "/paper.pdf";
      String altName = cuBase + doiSecond + ".pdf";
      //2018 options
      String pubJID = oneAM.getRaw(CrossRefSchemaHelper.pub_abbrev);
      String pubYear = oneAM.getRaw(CrossRefSchemaHelper.pub_year);
      String pubIssue = oneAM.getRaw(CrossRefSchemaHelper.pub_issue);
      String artPage = oneAM.getRaw(CrossRefSchemaHelper.art_sp);
      String name2018 = cuBase + doiFirst + "/" + pubJID + "." + pubYear + "." + pubIssue + "." + artPage + "/Paper.pdf";
      log.debug3("looking for: " + pdfName + " or " + altName + " or " + name2018);
      List<String> returnList = new ArrayList<String>();
      returnList.add(pdfName);
      returnList.add(altName);
      returnList.add(name2018);
      return returnList;
    }

     */
    
    @Override
    protected void postCookProcess(SourceXmlSchemaHelper schemaHelper, 
        CachedUrl cu, ArticleMetadata thisAM) {

    }
  }
}
