/*
 * $Id$
 */

/*

 Copyright (c) 2000-2017 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.plugin.taylorandfrancis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.cxf.common.util.StringUtils;
import org.lockss.util.*;
import org.lockss.daemon.*;
import org.lockss.extractor.*;

import org.lockss.plugin.CachedUrl;
import org.lockss.plugin.clockss.JatsPublishingSchemaHelper;
import org.lockss.plugin.clockss.SourceXmlMetadataExtractorFactory;
import org.lockss.plugin.clockss.SourceXmlSchemaHelper;


public class TaylorAndFrancisSourceXmlMetadataExtractorFactory extends SourceXmlMetadataExtractorFactory {
  private static final Logger log = Logger.getLogger(TaylorAndFrancisSourceXmlMetadataExtractorFactory.class);
  
  // 2013 triggered content only - now deprecated - leave in for backwards compat
  private static SourceXmlSchemaHelper TandFHelper = null;
  private static SourceXmlSchemaHelper TandF16Helper = null;
  // UACP_i_016_XXX_tandf.zip = tfdoc schema (volume 16 only)
  // otherwise use the regular article/unarticle schema
  private static final Pattern URL16_PATTERN = Pattern.compile("/UACP_i_016_[0-9sup]+_tandf\\.zip");
  private static final Pattern URL_UACP_PATTERN = Pattern.compile("/UACP_[^/]+\\.zip");
  private static final Pattern URL_WJPT_PATTERN = Pattern.compile("/WJPT_[^/]+\\.zip");
  private static final Pattern URL_LEGACY_SOURCE = Pattern.compile("2013/(UACP|WJPT)/");

  // 2017+ schema helper
  private static SourceXmlSchemaHelper TandFAtyponHelper = null;

  @Override
  public FileMetadataExtractor createFileMetadataExtractor(MetadataTarget target,
      String contentType)
          throws PluginException {
    return new TaylorAndFrancisSourceXmlMetadataExtractor();
  }

  public class TaylorAndFrancisSourceXmlMetadataExtractor extends SourceXmlMetadataExtractor {

    
    // BACKWARDS COMPATABILILTY - for 2013 triggered content delivery
    // In the bulk source we were given, for 2013/UACP and 2013/WJPT, the V16 of
    // UACP had xml files in a schema different from all the others! So this
    // setupSchema call matches the pattern of the URL and if it is UACP, V16
    // we return the necessary "tfdoc" schema, otherwise the "article/unarticle"
    // schema.
    // For all OTHER zip formats - just default to the current Atypon schema
    @Override
    protected SourceXmlSchemaHelper setUpSchema(CachedUrl cu) {
      // Once you have it created, just keep returning the same one. It won't change.

      Matcher legacySource = URL_LEGACY_SOURCE.matcher(cu.getUrl());
      if (legacySource.find()) {
        return getLegacySourceSchema(cu);
      }
      
      // Otherwise it's the standard case
      if (TandFAtyponHelper!= null) {
        return TandFAtyponHelper;
      }
      // This is very very much like JATS - but if it doesn't work exactly
      // then create a new AtyponJats because you don't want to destabilize the
      // standard
      TandFAtyponHelper = new JatsPublishingSchemaHelper();
      return TandFAtyponHelper;
    }
    
    protected SourceXmlSchemaHelper getLegacySourceSchema(CachedUrl cu) {
      // First - are we processing volume 16 of UACP? - if so, use tfdoc schema
      Matcher V16Mat = URL16_PATTERN.matcher(cu.getUrl()); 
      if (V16Mat.find()) {
        if (TandF16Helper != null) {
          return TandF16Helper;
        }
        TandF16Helper = new TaylorAndFrancisSourceTFDocXmlSchemaHelper();
        return TandF16Helper;
      }         
      // Otherwise it's the standard case
      if (TandFHelper != null) {
        return TandFHelper;
      }
      TandFHelper = new TaylorAndFrancisSourceXmlSchemaHelper();
      return TandFHelper;
    }
    

    // use the filename in the XML file and when it's not available (UACP V16)
    // send back the name of the current XML file with .pdf instead of .xml
    @Override
    protected List<String> getFilenamesAssociatedWithRecord(SourceXmlSchemaHelper helper, CachedUrl cu,
        ArticleMetadata oneAM) {
      
      String filenameValue = null;
      String filenameKey = helper.getFilenameXPathKey();
      // in V16, there is no filenameKey
      if (filenameKey != null) {
        filenameValue = oneAM.getRaw(filenameKey);
      }
      if (StringUtils.isEmpty(filenameValue)) {
        // just use the name of the XML file with ".pdf"
        // This will definitely happen in the case of V16 which uses diff schema
        // and for all new content
       filenameValue = FilenameUtils.getBaseName(cu.getUrl()) + ".pdf";
      }
      String cuBase = FilenameUtils.getFullPath(cu.getUrl());
      List<String> returnList = new ArrayList<String>();
      returnList.add(cuBase + filenameValue);
      return returnList;
    }
    
    private static final String PUBLISHER_NAME = "Taylor & Francis";
    private static final String WJPT_TITLE = "Journal of Pharmacy Teaching";
    private static final String UACP_TITLE = "Annals of Clinical Psychiatry";
    // One of the XML schema's doesn't set publisher. Do so here
    @Override
    protected void postCookProcess(SourceXmlSchemaHelper schemaHelper, 
        CachedUrl cu, ArticleMetadata thisAM) {
      Matcher UrlMat;
      if (thisAM != null) {
        thisAM.putIfBetter(MetadataField.FIELD_PUBLISHER, PUBLISHER_NAME);
        // The unarticle schema doesn't set a publication title, just set it
        UrlMat = URL_UACP_PATTERN.matcher(cu.getUrl());
        if (UrlMat.find()) {
          thisAM.putIfBetter(MetadataField.FIELD_PUBLICATION_TITLE, UACP_TITLE);
        } else {
          UrlMat = URL_WJPT_PATTERN.matcher(cu.getUrl());
          if (UrlMat.find()) {
            thisAM.putIfBetter(MetadataField.FIELD_PUBLICATION_TITLE, WJPT_TITLE);
          }

        }
        // use the alternate way of declaring authors to set
        List <String> altAuthList = null;
        if (thisAM.get(MetadataField.FIELD_AUTHOR) == null &&
            (altAuthList = thisAM.getRawList(JatsPublishingSchemaHelper.JATS_string_contrib)) != null) {
          for(int i = 0; i < altAuthList.size(); i++) {
            thisAM.put(MetadataField.FIELD_AUTHOR,altAuthList.get(i));
          }
        }
      }
      log.debug3("in TFSourceXmlMetadataExtractor postEmitProcess");
    }
    
  }
}
