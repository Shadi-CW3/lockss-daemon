/*
 * $Id$
 */

/*

 Copyright (c) 2000-2010 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.plugin.americansocietyofconsultantpharmacists;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.lockss.daemon.PluginException;
import org.lockss.extractor.*;
import org.lockss.extractor.XmlDomMetadataExtractor.NodeValue;
import org.lockss.extractor.XmlDomMetadataExtractor.TextValue;
import org.lockss.extractor.XmlDomMetadataExtractor.XPathValue;
import org.lockss.plugin.ArchivalUnit;
import org.lockss.plugin.CachedUrl;
import org.lockss.plugin.respediatrica.ResPediatricaHtmlMetadataExtractorFactory;
import org.lockss.util.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AmericanSocietyOfConsultantPharmacistsMetadataExtractorFactory
  implements FileMetadataExtractorFactory {
  static Logger log = 
    Logger.getLogger(AmericanSocietyOfConsultantPharmacistsArticleIteratorFactory.class);

  @Override
  public FileMetadataExtractor createFileMetadataExtractor(MetadataTarget target,
                                                           String contentType)
          throws PluginException {
    return new ResPediatricaHtmlMetadataExtractorFactory.PediatricaOaiHtmlMetadataExtractor();
  }

/*
  <meta name="DC.title" content="Rhinorrhea as a Result of Alzheimer&#039;s Disease Treatment"/>
  <meta name="DC.type" content="Text"/>
  <meta name="DC.publisher" content="American Society of Consultant Pharmacists"/>
  <meta name="DC.creator" content="Vouri, Scott Martin"/>
  <meta name="DC.identifier" content="info:doi/10.4140/TCP.n.2020.148."/>
  <meta name="DCTERMS.issued" content="April 2020"/>
  <meta name="DCTERMS.bibliographicCitation" content="The Senior Care Pharmacist, 35, 4, 148-149(2)"/>
  <meta name="DCTERMS.isPartOf" content="urn:ISSN:2639-9636"/>
  <meta name="IC.identifier" content="ascp/tscp/2020/00000035/00000004"/>
  <meta name="CRAWLER.fullTextLink" content="https://api.ingentaconnect.com/content/ascp/tscp/2020/00000035/00000004?crawler=true"/>
   */

  public static class PediatricaOaiHtmlMetadataExtractor
          extends SimpleHtmlMetaTagMetadataExtractor {
    private static MultiMap tagMap = new MultiValueMap();
    static {
      tagMap.put("DC.title", MetadataField.DC_FIELD_TITLE);
      tagMap.put("DC.publisher", MetadataField.DC_FIELD_PUBLISHER);
      tagMap.put("DC.type", MetadataField.DC_FIELD_TYPE);
      tagMap.put("DC.creator", MetadataField.DC_FIELD_CREATOR);
      tagMap.put("DC.identifier", MetadataField.DC_FIELD_IDENTIFIER);
      tagMap.put("DCTERMS.issued", MetadataField.DC_FIELD_DATE);
      tagMap.put("DCTERMS.isPartOf", MetadataField.DC_FIELD_IDENTIFIER_ISSN);
      
    }

    @Override
    public ArticleMetadata extract(MetadataTarget target, CachedUrl cu)
            throws IOException {
      ArticleMetadata am = super.extract(target, cu);
      am.cook(tagMap);
      String url = am.get(MetadataField.FIELD_ACCESS_URL);
      ArchivalUnit au = cu.getArchivalUnit();
      if (url == null || url.isEmpty() || !au.makeCachedUrl(url).hasContent()) {
        url = cu.getUrl();
      }
      am.replace(MetadataField.FIELD_ACCESS_URL,url);
      return am;
    }
  }
}