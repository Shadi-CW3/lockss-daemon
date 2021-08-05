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

package org.lockss.plugin.clockss.eastview;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.lockss.extractor.MetadataField;
import org.lockss.extractor.XmlDomMetadataExtractor;
import org.lockss.extractor.XmlDomMetadataExtractor.NodeValue;
import org.lockss.extractor.XmlDomMetadataExtractor.XPathValue;
import org.lockss.plugin.clockss.SourceXmlSchemaHelper;
import org.lockss.util.Logger;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 *  A helper class that defines a schema for XML metadata extraction for
 *  Eastview Information Services
 *  
 */
public class EastviewJournalMetadataXhtmlFormatHelper
implements SourceXmlSchemaHelper {
  
  private static final Logger log = Logger.getLogger(EastviewJournalMetadataXhtmlFormatHelper.class);
  static protected final String PUBLICATION_TITLE_PATH = "/html/head/title";
  
  /*
   * <PAGE>3</PAGE>
   * or
   * <PAGE>3-6</PAGE>
   */
  private final static NodeValue STARTPAGE_VALUE = new NodeValue() {
    @Override
    public String getValue(Node node) {
      if (node == null) {
        return null;
      }
      log.debug3("getValue of PAGE - interpret for start page");
      String pageVal = node.getTextContent();
      if (pageVal != null)  {
        return StringUtils.substringBefore(pageVal,"-");
      } else {
        log.debug3("no page value set");
        return null;
      }
    }
  };
  
  private final static NodeValue CLEAN_TEXT = new NodeValue() {
    @Override
    public String getValue(Node node) {
      if (node == null) {
        return null;
      }
      log.debug3("Cleaning text value - strip lead/trail space");
      return StringUtils.strip(node.getTextContent());
    }
  };

  // For journals, we treat each page as a seperate article
  private static final String top = "//html/body";
  

  /*
   *  The following 3 variables are needed to use the XPathXmlMetadataParser
   */

  /* 1.  MAP associating xpath & value type definition or evaluator */
  static private final Map<String,XPathValue>     
  articleMap = new HashMap<String,XPathValue>();
  static {
    articleMap.put(PUBLICATION_TITLE_PATH, CLEAN_TEXT);

  }

  /* 2.  Top level per-article node */
  static private final String articleNode = top;

  /* 3. Global metadata is the publisher - work around if it gets troublesome */
  static private final Map<String, XPathValue> 
    globalMap = null; 
  /*
   * The emitter will need a map to know how to cook raw values
   */
  private static final String AUTHOR_SPLIT_CH = ",";
  protected static final MultiValueMap cookMap = new MultiValueMap();
  static {
    cookMap.put(PUBLICATION_TITLE_PATH, MetadataField.FIELD_PUBLICATION_TITLE);
  }

  /**
   * publisher comes from a global node
   */
  @Override
  public Map<String, XPathValue> getGlobalMetaMap() {
    //no globalMap, so returning null
    return globalMap; 
  }

  /**
   * return  article paths representing metadata of interest  
   */
  @Override
  public Map<String, XPathValue> getArticleMetaMap() {
    return articleMap;
  }

  /**
   * Return the article node path
   */
  @Override
  public String getArticleNode() {
    return articleNode;
  }

  /**
   * Return a map to translate raw values to cooked values
   */
  @Override
  public MultiValueMap getCookMap() {
    return cookMap;
  }

  /**
   */

  @Override
  public String getDeDuplicationXPathKey() {
    return null;
  }

  /**
   * Return the path for product form so when multiple records for the same
   * item are combined, the product forms are combined together
   */

  @Override
  public String getConsolidationXPathKey() {
    return null;
  }

  /**
   * using filenamePrefix (see above)
   */

  @Override
  public String getFilenameXPathKey() {
    return null;
  }

}