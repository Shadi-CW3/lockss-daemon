/*
 * $Id: OaiPmhHtmlFilterFactory.java,v 1.1.2.1 2014/05/05 17:32:30 wkwilson Exp $
 */

/*

Copyright (c) 2000-2012 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.plugin.springer.link;

import java.io.InputStream;
import java.io.Reader;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.*;
import org.lockss.filter.FilterUtil;
import org.lockss.filter.WhiteSpaceFilter;
import org.lockss.filter.html.*;
import org.lockss.plugin.*;
import org.lockss.util.ReaderInputStream;

public class SpringerLinkHtmlHashFilterFactory implements FilterFactory {

    public InputStream createFilteredInputStream(ArchivalUnit au,
                                                 InputStream in,
                                                 String encoding) {
        NodeFilter[] filters = new NodeFilter[] {
          HtmlNodeFilters.tag("script"),
          HtmlNodeFilters.tag("noscript"),
          HtmlNodeFilters.tag("input"),
          HtmlNodeFilters.tag("head"),

          //google iframes with weird ids
          HtmlNodeFilters.tag("iframe"),

          //footer
          HtmlNodeFilters.tagWithAttribute("div", "id", "footer"),

          //more links to pdf and article
          HtmlNodeFilters.tagWithAttribute("div", "class", "bar-dock"),

          //weird meta tag
          HtmlNodeFilters.tagWithAttribute("meta", "name", "nolard"),

          //adds on the side
          HtmlNodeFilters.tagWithAttribute("div", "class", "banner-advert"),
          HtmlNodeFilters.tagWithAttribute("div", "id", "doubleclick-ad"),

          //header and search box
          HtmlNodeFilters.tagWithAttribute("div", "id", "header"),
          HtmlNodeFilters.tagWithAttribute("div", "role", "banner"),

          //non essentials like metrics and related links
          HtmlNodeFilters.tagWithAttribute("div", "role", "complementary"),
          HtmlNodeFilters.tagWithAttribute("div", "class", "col-aside"),
          HtmlNodeFilters.tagWithAttribute("div", "class", "document-aside"),

          //random divs floating around
          HtmlNodeFilters.tagWithAttribute("div", "id", "MathJax_Message"),
          HtmlNodeFilters.tagWithAttribute("div", "id", "web-trekk-abstract"),
          HtmlNodeFilters.tagWithAttribute("div", "class", "look-inside-interrupt"),
          HtmlNodeFilters.tagWithAttribute("div", "id", "colorbox"),
          HtmlNodeFilters.tagWithAttribute("div", "id", "cboxOverlay"),
          HtmlNodeFilters.tagWithAttribute("div", "id", "gimme-satisfaction"),
          HtmlNodeFilters.tagWithAttribute("div", "class", "crossmark-tooltip"),
                                                 
        };
        
        HtmlFilterInputStream filteredStream = new HtmlFilterInputStream(in,
                                          encoding,
                                          HtmlNodeFilterTransform.exclude(new OrFilter(filters)));
        
        Reader filteredReader = FilterUtil.getReader(filteredStream, encoding);
        
        // Remove white space
        return new ReaderInputStream(new WhiteSpaceFilter(filteredReader));
    }
    
}
