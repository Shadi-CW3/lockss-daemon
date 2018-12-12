/*
 * $Id$
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

package org.lockss.plugin.silvafennica;

import java.io.*;

import org.apache.commons.io.input.CountingInputStream;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.OrFilter;
import org.lockss.daemon.PluginException;
import org.lockss.filter.*;
import org.lockss.filter.HtmlTagFilter.TagPair;
import org.lockss.filter.html.*;
import org.lockss.plugin.*;
import org.lockss.util.Logger;
import org.lockss.util.ReaderInputStream;

public class FS2HtmlHashFilterFactory implements FilterFactory {

  /*
   * 
   */
  private static final Logger log = Logger.getLogger(FS2HtmlHashFilterFactory.class);
  
  protected static NodeFilter[] excludeFilters = new NodeFilter[] {
      // DROP scripts, styles, comments
      HtmlNodeFilters.tag("script"),
      HtmlNodeFilters.tag("noscript"),
      HtmlNodeFilters.tag("style"),
      HtmlNodeFilters.comment(),
//      HtmlNodeFilters.tagWithAttributeRegex("a", "class", "comment"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "comment"),
      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "(header|menu|footer)"),
      HtmlNodeFilters.tagWithAttribute("p", "class", "references"),
//      HtmlNodeFilters.tagWithAttributeRegex("a", "href", "^/article/[0-9]+/(author|ref|selected)/"),
//      HtmlNodeFilters.tagWithAttributeRegex("a", "href", "/issue//?(author|keyword)/"),
      
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "graphic-wrap"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "toolbar-wrap"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "^ref-list"),
//      // DROP eventual "Free"/"Open Access" text/icon [AMA/SPIE TOC/article]
//      HtmlNodeFilters.tagWithAttributeRegex("span", "class", "freeArticle"), // [AMA TOC/article]
//      HtmlNodeFilters.tagWithText("h4", "Open Access"), // [SPIE TOC/article]
//      HtmlNodeFilters.tagWithAttributeRegex("i", "class", "icon-availability"),
//      // DROP RSS and e-mail alert buttons [AMA/SPIE TOC]
//      HtmlNodeFilters.tagWithAttribute("div", "class", "subscribe"),
//      // DROP expand/collapse buttons [AMA/SPIE TOC]
//      HtmlNodeFilters.tagWithAttribute("div", "class", "expandCollapse"),
//      // DROP previous/next article link text [AMA/SPIE TOC/article]
//      // (also in crawl filter)
//      HtmlNodeFilters.tagWithAttribute("a", "class", "prev"),
//      HtmlNodeFilters.tagWithAttribute("a", "class", "next"),
//      // DROP designated separator
//      // [AMA article]: vertical bar in breadcrumb
//      // [SPIE article]: semicolon between authors
//      HtmlNodeFilters.tagWithAttribute("span", "class", "separator"),
//      // DROP text size picker [AMA/SPIE article]
//      HtmlNodeFilters.tagWithAttribute("div", "class", "textSize"),
//      // DROP internal jump links [AMA/SPIE article]
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "contentJumpLinks"),
//      // DROP sections appended to end of main area [AMA/SPIE article]
//      HtmlNodeFilters.tagWithAttribute("div", "id", "divSignInSubscriptionUpsell"),
//      HtmlNodeFilters.tagWithAttribute("div", "class", "relatedArticlesMobile"),
//      HtmlNodeFilters.tagWithAttribute("div", "class", "collectionsMobile"),
//      // DROP parts of figures/tables other than captions [AMA/SPIE article/figures/tables]
//      HtmlNodeFilters.allExceptSubtree(HtmlNodeFilters.tagWithAttributeRegex("div", "class", "figureSection"),
//          new OrFilter(HtmlNodeFilters.tagWithAttributeRegex("h6", "class", "figureLabel"), // [AMA article/figures]
//              HtmlNodeFilters.tagWithAttribute("div", "class", "figureCaption"))), // [AMA/SPIE article/figures]
//      HtmlNodeFilters.allExceptSubtree(HtmlNodeFilters.tagWithAttributeRegex("div", "class", "tableSection"),
//          new OrFilter(HtmlNodeFilters.tagWithAttribute("div", "class", "tableCaption"), // [SPIE article/tables]
//              HtmlNodeFilters.tagWithAttributeRegex("span", "class", "^Table "))), // [AMA article/tables]
//      HtmlNodeFilters.tagWithAttributeRegex("span", "class", "^Figure "), // freeform, e.g. http://jama.jamanetwork.com/article.aspx?articleid=1487499 [AMA]
//      // DROP Letters(7), CME(8), Citing(9) & Responses(10) tabs and panels [AMA  article]
//      // DROP Figures(3),Tables(4) because they are in random order when presented outside
//      // of the Article(1) view
//      // [SPIE]: has only (10) for now
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "tab(3|4|7|8|9|10)Div"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "id", "^tab(3|4|7|8|9|10)$"),
//      // DROP external links in References [AMA/SPIE article/references]
//      HtmlNodeFilters.tagWithAttributeRegex("span", "class", "pubmedLink"), // [AMA article/references]
//      HtmlNodeFilters.tagWithAttributeRegex("span", "class", "crossrefDoi"), // [AMA/SPIE article/references]
//      // First page preview sometimes appears, sometimes not [AMA article]
//      HtmlNodeFilters.tagWithAttribute("div", "id", "divFirstPagePreview"),
//      HtmlNodeFilters.tagWithAttribute("div", "class", "adPortlet"), // rightside ads
//      HtmlNodeFilters.tagWithAttribute("div", "class", "portletContentHolder"), // rightside
//      // related content and metrics
//      //figure links
//      HtmlNodeFilters.tagWithAttribute("div", "class", "figurelinks"),
//      //figures section changes
//      HtmlNodeFilters.tagWithAttribute("div", "class", "abstractFigures"),
//      // ASHA - additions
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "widget-AltmetricLink"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "widget-CitingArticles"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "widget-ArticleLinks"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "widget-Toolbox"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "access-state-logos"),
//      HtmlNodeFilters.tagWithAttributeRegex("span", "class", "article-groups"),
//      // copyright date did not have a tag suitable for filtering, using containing tag for ASHA
//      HtmlNodeFilters.tagWithAttribute("div", "id", "getCitation"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "copyright"),
//      // changeable
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "article-metadata"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "terms-wrapper"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "related.topic"),
  };
  
  NodeFilter[] includeFilters = new NodeFilter[] {
      // <div class="column_2">
      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "column_2"),
//      HtmlNodeFilters.tagWithAttribute("div", "class", "articleBodyContainer"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "contentColumn"),
//      HtmlNodeFilters.tagWithAttribute("span", "class", "citationCopyPaste"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "content-section"),
//      HtmlNodeFilters.tagWithAttribute("div", "id", "ArticleList"),
//      HtmlNodeFilters.tagWithAttributeRegex("div", "class", "leftColumn"),
  };
  
  @Override
  public InputStream createFilteredInputStream(final ArchivalUnit au,
                                               InputStream in,
                                               String encoding)
      throws PluginException {
    
    /*
     * KEEP: throw out everything but main content areas
     * DROP: filter remaining content areas
     */
    HtmlCompoundTransform compoundTransform =
        new HtmlCompoundTransform(
            HtmlNodeFilterTransform.include(new OrFilter(includeFilters)),
            HtmlNodeFilterTransform.exclude(new OrFilter(excludeFilters)));

    InputStream filtered = new HtmlFilterInputStream(in, encoding, encoding, compoundTransform);
    Reader reader = FilterUtil.getReader(filtered, encoding);

    // Remove all inner tag content
    Reader noTagFilter = new HtmlTagFilter(new StringFilter(reader, "<", " <"), new TagPair("<", ">"));

    // Remove white space
    Reader whiteSpaceFilter = new WhiteSpaceFilter(noTagFilter);
    InputStream ret;

    ret = new ReaderInputStream(whiteSpaceFilter);
    // Instrumentation
    return new CountingInputStream(ret) {
      @Override
      public void close() throws IOException {
        long bytes = getByteCount();
        if (bytes <= 100L) {
          log.debug(String.format("%d byte%s in %s", bytes, bytes == 1L ? "" : "s", au.getName()));
        }
        if (log.isDebug2()) {
          log.debug2(String.format("%d byte%s in %s", bytes, bytes == 1L ? "" : "s", au.getName()));
        }
        super.close();
      }
    };
  }
  
}
