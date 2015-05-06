/*
 * $Id$
 */

/*

Copyright (c) 2000-2015 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.plugin.atypon.multiscience;

import java.io.InputStream;
import java.util.Vector;

import org.htmlparser.*;
import org.htmlparser.tags.*;
import org.lockss.filter.html.HtmlNodeFilters;
import org.lockss.plugin.ArchivalUnit;
import org.lockss.plugin.atypon.BaseAtyponHtmlHashFilterFactory;

// Keeps contents only (includeNodes), then hashes out unwanted nodes 
// within the content (excludeNodes).
public class MultiScienceAtyponHtmlHashFilterFactory 
  extends BaseAtyponHtmlHashFilterFactory  {
     
  @Override
  public InputStream createFilteredInputStream(ArchivalUnit au,
                                               InputStream in, 
                                               String encoding) {
    NodeFilter[] includeNodes = new NodeFilter[] {
        // ?? review when manifest pages up
        // manifest pages
        // <ul> and <li> without attributes (unlike TOC/abs/ref breadcrumbs)
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (HtmlNodeFilters.tagWithAttributeRegex("a", "href", 
                                                      "/toc/").accept(node)) {
              Node liParent = node.getParent();
              if (liParent instanceof Bullet) {
                Bullet li = (Bullet)liParent;
                Vector liAttr = li.getAttributesEx();
                if (liAttr != null && liAttr.size() == 1) {
                  Node ulParent = li.getParent();
                  if (ulParent instanceof BulletList) {
                    BulletList ul = (BulletList)ulParent;
                    Vector ulAttr = ul.getAttributesEx();
                    return ulAttr != null && ulAttr.size() == 1;
                  }
                }
              }
            }
            return false;
          }
        },
        // toc - contents only
        // http://multi-science.atypon.com/toc/ijfc/6/1
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "tocListWidget"),
        // abs, ref - contents only
        // http://multi-science.atypon.com/doi/abs/10.1260/1756-8250.6.1.43
        // http://multi-science.atypon.com/doi/ref/10.1260/1756-8250.6.1.61
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", 
                                          "literatumPublicationContentWidget"),
        // showCitFormats
        // http://multi-science.atypon.com/action/
        //                      showCitFormats?doi=10.1260%2F1756-8250.6.1.43
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", 
                                              "downloadCitationsWidget"),
                                                                  
    };
    
    // handled by parent: script, sfxlink, stylesheet, pdfplus file sise
    // <head> tag, <li> item has the text "Cited by", accessIcon, 
    NodeFilter[] excludeNodes = new NodeFilter[] {
        // handled by parent:
        // <img src="/templates/jsp/_style2/_metaPress/images/access_full.gif" 
        // alt="Full Access" title="Full Access" class="accessIcon fullAccess">
        
        // toc - select pulldown menu under volume title
        //<div class="publicationToolContainer">
        HtmlNodeFilters.tagWithAttributeRegex("div", "class",
                                              "publicationToolContainer"),
        // abs - scattering - potentially generated code added (like
        // Endocrine Society)                                      
        // http://multi-science.atypon.com/doi/abs/10.1260/1756-8250.6.1.43
        // <div class="articleMetaDrop publicationContentDropZone" data-pb-dropzone="articleMetaDropZone">                                      
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", 
                                              "articleMetaDrop"),   
        
       // ?? perhaps it's not necessary                                       
       // abs - all right column except Citation Mgr (download citations)
       // http://multi-science.atypon.com/doi/abs/10.1260/1756-8250.6.1.43                                      
       //HtmlNodeFilters.allExceptSubtree(
       //    HtmlNodeFilters.tagWithAttributeRegex("div", "class", "articleTools"),
       //      HtmlNodeFilters.tagWithAttributeRegex(
       //             "a", "href", "/action/showCitFormats\\?")),                                           
                                                    
    };
    return super.createFilteredInputStream(au, in, encoding, 
                                           includeNodes, excludeNodes);
  }

  @Override
  public boolean doTagIDFiltering() {
    return true;
  }
  
  @Override
  public boolean doWSFiltering() {
    return true;
  }
  
}
