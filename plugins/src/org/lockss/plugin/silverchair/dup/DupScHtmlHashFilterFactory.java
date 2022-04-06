/*

Copyright (c) 2000-2022, Board of Trustees of Leland Stanford Jr. University

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

package org.lockss.plugin.silverchair.dup;

import org.htmlparser.NodeFilter;
import org.lockss.daemon.PluginException;
import org.lockss.filter.html.HtmlNodeFilters;
import org.lockss.plugin.ArchivalUnit;
import org.lockss.plugin.silverchair.BaseScHtmlHashFilterFactory;
import org.lockss.util.Logger;

import java.io.InputStream;

public class DupScHtmlHashFilterFactory extends BaseScHtmlHashFilterFactory {

  private static final Logger log = Logger.getLogger(DupScHtmlHashFilterFactory.class);
  
  @Override
  protected boolean doExtraSpecialFilter() {
    return false;
  }

  @Override
  public InputStream createFilteredInputStream(final ArchivalUnit au,
                                               InputStream in,
                                               String encoding)
      throws PluginException {
    
    NodeFilter[] includeFilters = new NodeFilter[] {
            HtmlNodeFilters.tagWithAttribute("div", "class", "article"),
            HtmlNodeFilters.tagWithAttribute("div", "class", "widget"),
            HtmlNodeFilters.tagWithAttribute("div", "class", "article-body"),
            //https://read.dukeupress.edu/jhppl/issue/43/1, need to get content from this page
            HtmlNodeFilters.tagWithAttribute("div", "class", "al-article-items"),
            HtmlNodeFilters.tagWithAttributeRegex("div", "class", "fig-section"),
            // https://read.dukeupress.edu/american-literature/list-of-issues/2018
            HtmlNodeFilters.tagWithAttributeRegex("div", "class", "widget-ContentBrowseByYearManifest "),
    };

    NodeFilter[] moreExcludeFilters = new NodeFilter[] {
            HtmlNodeFilters.tagWithAttributeRegex("h2", "class", "backreferences-title"),

    };

    
    return createFilteredInputStream(au, in, encoding, includeFilters, moreExcludeFilters);
  }
  
}
