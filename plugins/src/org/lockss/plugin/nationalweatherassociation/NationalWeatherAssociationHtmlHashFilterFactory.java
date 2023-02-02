/*

Copyright (c) 2000-2023, Board of Trustees of Leland Stanford Jr. University

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

package org.lockss.plugin.nationalweatherassociation;

import java.io.InputStream;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.*;
import org.lockss.filter.html.*;
import org.lockss.plugin.*;

/*
 * This journal does not have list of issues. Articles are listed 
 * by year/volume.
 *  journal home - <nwabase>.org/<journal_id>/
 *  volume toc - <nwabase>.org/<journal_id>/publications2013.php
 *  start url/volume page - 
 *      <nwabase>.org/<journal_id>/include/publications2013.php
 *  abstract  - 
 *      <nwabase>.org/<journal_id>/abstracts/2013/2013-JOM22/abstract.php
 */
public class NationalWeatherAssociationHtmlHashFilterFactory 
  implements FilterFactory {
  
  public InputStream createFilteredInputStream(ArchivalUnit au, 
                                               InputStream in,
                                               String encoding) {

    NodeFilter[] filters = new NodeFilter[] {
        new TagNameFilter("script"),
        // filter out comments
        HtmlNodeFilters.comment(),
        // stylesheets
        HtmlNodeFilters.tagWithAttribute("link", "rel", "stylesheet"),
        // top header
        HtmlNodeFilters.tagWithAttribute("div", "id", "header"),
        // left sidebar
        HtmlNodeFilters.tagWithAttribute("div", "id", "left"),
        // right sidebar deadlines
        HtmlNodeFilters.tagWithAttribute("div", "id", "deadlines"),
        // footer
        HtmlNodeFilters.tagWithAttribute("div", "id", "footer"),
        // banner
        HtmlNodeFilters.tagWithAttributeRegex("img", "src", "banner\\.png"),
        // last updated date
        HtmlNodeFilters.tagWithTextRegex("p", ".*Last updated.*", true), 
    };

    return new HtmlFilterInputStream(in, encoding, 
        HtmlNodeFilterTransform.exclude(new OrFilter(filters)));
  }
    
}
