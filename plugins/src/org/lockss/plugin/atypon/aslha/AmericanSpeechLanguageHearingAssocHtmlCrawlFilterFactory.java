/* $Id$
 */

/*

Copyright (c) 2018 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.plugin.atypon.aslha;

import java.io.InputStream;

import org.htmlparser.NodeFilter;
import org.lockss.daemon.PluginException;
import org.lockss.filter.html.*;
import org.lockss.plugin.*;
import org.lockss.plugin.atypon.BaseAtyponHtmlCrawlFilterFactory;

/*
 *
 * created because article links are not grouped under a journalid or volumeid,
 * but under article ids - will pull the links from the page, so filtering out
 * extraneous links
 * 
 */
public class AmericanSpeechLanguageHearingAssocHtmlCrawlFilterFactory extends BaseAtyponHtmlCrawlFilterFactory {

  NodeFilter[] filters = new NodeFilter[] {
          // NOTE: overcrawling is an occasional issue with in-line references to "original article"
          HtmlNodeFilters.tag("header"),
          HtmlNodeFilters.tag("footer"),

          // Remove from toc page
          HtmlNodeFilters.tagWithAttributeRegex("div", "class", "page-top-banner"),
          // Remove from toc page
          HtmlNodeFilters.tagWithAttributeRegex("div", "class", "publication__menu"),
          // Remove from toc page
          // Need to download citation from this div
          HtmlNodeFilters.allExceptSubtree(
                  HtmlNodeFilters.tagWithAttribute("div", "class", "actionsbar"),
                  HtmlNodeFilters.tagWithAttributeRegex(
                          "a", "href", "/action/showCitFormats\\?")),
          // Remove from toc page
          HtmlNodeFilters.tagWithAttributeRegex("div", "class", "social-menus"),
          // Remove from toc page
          HtmlNodeFilters.tagWithAttributeRegex("div", "class", "table-of-content__navigation"),
          // Remove from toc page, this is the birdview image
          HtmlNodeFilters.tagWithAttributeRegex("div", "class", "current-issue"),
          // Remove from toc page
          HtmlNodeFilters.tagWithAttributeRegex("div", "class", "advertisement"),

          // Remove from doi/full page
          HtmlNodeFilters.tagWithAttributeRegex("div", "class", "page-top-panel"),
          // Remove from doi/full page
          HtmlNodeFilters.tagWithAttributeRegex("ul", "class", "tab__nav"),
          // Remove from doi/full page
          HtmlNodeFilters.tagWithAttributeRegex("ul", "class", "tab__content"),
          // Remove from doi/full page
          HtmlNodeFilters.tagWithAttributeRegex("div", "class", "eCommercePurchaseAccessWidget")

  };
  
  public InputStream createFilteredInputStream(ArchivalUnit au,
      InputStream in, String encoding) throws PluginException{ 
    return super.createFilteredInputStream(au, in, encoding, filters);
  }
  
}

