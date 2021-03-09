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

package org.lockss.plugin.atypon.nrcresearchpress;

import java.io.InputStream;
import org.htmlparser.NodeFilter;
import org.lockss.filter.html.*;
import org.lockss.plugin.*;
import org.lockss.plugin.atypon.BaseAtyponHtmlHashFilterFactory;
import org.lockss.util.Logger;

public class ClockssNRCResearchPressHtmlHashFilterFactory extends BaseAtyponHtmlHashFilterFactory {
  protected static final Logger log = Logger.getLogger(ClockssNRCResearchPressHtmlHashFilterFactory.class);
  
  @Override
  public InputStream createFilteredInputStream(ArchivalUnit au,
                                               InputStream in,
                                               String encoding) {
    NodeFilter[] nrcfilters = new NodeFilter[] {
        // hash filter
        // comments, scripts, header, footer, citedBySection handled by parent
        // top header
        HtmlNodeFilters.tagWithAttribute("div",  "id", "top-bar-wrapper"),
        // banner 
        HtmlNodeFilters.tagWithAttribute("div",  "class", "banner"),
        HtmlNodeFilters.tagWithAttribute("div",  "id", "nav-wrapper"),
        HtmlNodeFilters.tagWithAttribute("div",  "id", "breadcrumbs"),
        HtmlNodeFilters.tagWithAttribute("table",  "class", "mceItemTable"),
        // Remove link to "citing articles" because it only appears after an
        // article has cited this one - enclosing <li> will be extra
        // but this will stabilize 
        HtmlNodeFilters.tagWithAttribute("a",  "class", "icon-citing"), 
        // Remove link to "also read" 
        HtmlNodeFilters.tagWithAttribute("a",  "class", "icon-recommended"), 
        // Do NOT hash out link to related articles, because it could be 
        // a correction/corrected article link which we need to know about
        // <a class="icon-related">
        // hash out entire left sidebar
        HtmlNodeFilters.tagWithAttribute("div",  "id", "sidebar-left"), 
        //can't crawl filter this because it has citation download link, but ok
        //to hash
        HtmlNodeFilters.tagWithAttribute("div",  "id", "sidebar-right"), 
    };

    // super.createFilteredInputStream adds nrc filter to the baseAtyponFilters
    // and returns the filtered input stream using an array of NodeFilters that 
    // combine the two arrays of NodeFilters.
    return super.createFilteredInputStream(au, in, encoding, nrcfilters);
  }

}
