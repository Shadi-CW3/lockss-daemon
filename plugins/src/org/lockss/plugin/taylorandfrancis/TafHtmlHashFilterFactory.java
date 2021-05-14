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

package org.lockss.plugin.taylorandfrancis;

import java.io.*;
import java.util.regex.Pattern;

import org.apache.commons.io.input.CountingInputStream;
import org.htmlparser.*;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.*;
import org.htmlparser.util.NodeList;
import org.lockss.daemon.PluginException;
import org.lockss.filter.*;
import org.lockss.filter.HtmlTagFilter.TagPair;
import org.lockss.filter.html.*;
import org.lockss.plugin.*;
import org.lockss.util.*;

/**
 *
 */
public class TafHtmlHashFilterFactory implements FilterFactory {

  private static final Logger log = Logger.getLogger(TafHtmlHashFilterFactory.class);

  public boolean isSiblingDivWAttrEmpty(Node node, String attribute, String attrValue ) {
    Node siblingDiv = findSiblingDivByAttr(node, attribute, attrValue);
    if (siblingDiv != null) {
      Node siblingDivChild = findAnyChildDiv(siblingDiv.getFirstChild());
      if (siblingDivChild != null) {
        // if a child of the figureTablesPanel exists, then that tab is active
        // discard the abstract
        return true;
      }
    }
    return false;
  }

  public Node findAnyChildDiv(Node sibling) {
    Node targetSibling = null;
    while (sibling != null) {
      if (sibling instanceof Div) {
        targetSibling = sibling;
        break;
      }
      sibling = sibling.getNextSibling();
    }
    return targetSibling;
  }

  public static NodeFilter anyTagAttributeRegexFilter(String attr, String attrValue) {
    return new HtmlNodeFilters.HasAttributeRegexFilter(attr, attrValue, true);
  };
  public Node findSiblingDivByAttr(Node sibling, String attribute, String attrValue) {
    Node targetSibling = null;
    String thisID;
    while (sibling != null) {
      if (sibling instanceof Div) {
        thisID = ((Div) sibling).getAttribute(attribute);
        if (thisID != null && !thisID.isEmpty() && thisID.contains(attrValue)) {
          targetSibling = sibling;
          break;
        }
      }
      sibling = sibling.getNextSibling();
    }
    return targetSibling;
  }

  public boolean tagWithAttrNotDescendantWithAttr(Node node,
                                                  String nodeAttr,
                                                  String nodeVal,
                                                  String ancestorAttr,
                                                  String ancestorVal) {
    boolean returnAble = false;
    if (node instanceof Tag) {
      String attr = ((Tag) node).getAttribute(nodeAttr);
      if(attr != null && !attr.isEmpty() && attr.contains(nodeVal)) {
        returnAble = true;
        Node ancestor = node.getParent();
        while (ancestor != null) {
          if (ancestor instanceof Tag) {
            String ancAttr = ((Tag) ancestor).getAttribute(ancestorAttr);
            if (ancAttr != null && !ancAttr.isEmpty() && ancAttr.equals(ancestorVal)) {
              returnAble = false;
              break;
            }
            ancestor = ancestor.getParent();
          }
        }
      }
    }
    return returnAble;
  }

  public boolean tagWithAttrNotDescendantWithAttr(Node node,
                                                  String nodeAttr,
                                                  String nodeVal,
                                                  String ancestorAttr,
                                                  String ancestorVal,
                                                  String ancestorAttr2,
                                                  String ancestorVal2) {
    boolean returnAble = false;
    if (node instanceof Tag) {
      String attr = ((Tag) node).getAttribute(nodeAttr);
      if(attr != null && !attr.isEmpty() && attr.contains(nodeVal)) {
        returnAble = true;
        Node ancestor = node.getParent();
        while (ancestor != null) {
          if (ancestor instanceof Tag) {
            String ancAttr = ((Tag) ancestor).getAttribute(ancestorAttr);
            String ancAttr2 = ((Tag) ancestor).getAttribute(ancestorAttr2);
            if ( (ancAttr != null && !ancAttr.isEmpty() && ancAttr.equals(ancestorVal)) ||
                 (ancAttr2 != null && !ancAttr2.isEmpty() && ancAttr2.equals(ancestorVal2)) ){
              returnAble = false;
              break;
            }
            ancestor = ancestor.getParent();
          }
        }
      }
    }
    return returnAble;
  }
  public boolean tagWithAttrIsDescendentWithAttr(Node node,
                                                  String nodeAttr,
                                                  String nodeVal,
                                                  String ancestorAttr,
                                                  String ancestorVal) {
    boolean returnAble = false;
    if (node instanceof Tag) {
      String attr = ((Tag) node).getAttribute(nodeAttr);
      if(attr != null && !attr.isEmpty() && attr.contains(nodeVal)) {
        Node ancestor = node.getParent();
        while (ancestor != null) {
          if (ancestor instanceof Tag) {
            String ancAttr = ((Tag) ancestor).getAttribute(ancestorAttr);
            if (ancAttr != null && !ancAttr.isEmpty() && ancAttr.equals(ancestorVal)) {
              returnAble = true;
              break;
            }
            ancestor = ancestor.getParent();
          }
        }
      }
    }
    return returnAble;
  }

    /**
     * <p>
     * This node filter selects all nodes in a target tree (characterized by a
     * target root node filter), except for the nodes in any number of
     * designated subtree(s) (characterized by a designated list of subtree
     * node filters) and all nodes on the direct path from the target root to
     * the designated subtree.
     * </p>
     */
  public static class allExceptAnySubtree implements NodeFilter {

    protected NodeFilter rootNodeFilter;
    protected NodeFilter[] subTreeNodeFilters;

    public allExceptAnySubtree(NodeFilter rootNodeFilter,
        NodeFilter[] subTreeNodeFilters) {
      this.rootNodeFilter = rootNodeFilter;
      this.subTreeNodeFilters = subTreeNodeFilters;
    }

    @Override
    public boolean accept(Node node) {
      // Inspect the node's ancestors
      for (Node current = node ; current != null ; current = current.getParent()) {
        for (NodeFilter filter : subTreeNodeFilters) {
          if (filter.accept(current)) {
            // The node is in the designated subtree: don't select it (meaning,
            // return false).
            return false;
          }
        }
        if (rootNodeFilter.accept(current)) {
          // The node is in the target tree. Selecting it or not depends on
          // whether it is on the path from the target root to the designated
          // subtree or not.
          NodeList nl = new NodeList();
          for (NodeFilter filter : subTreeNodeFilters) {
            node.collectInto(nl, filter);
          }
          // If the node list is empty, the node is not on the path from the
          // target root to the designated subtree: select it (meaning, return
          // true). If the node list is non-empty, the node is on the path from
          // the target root to the designated subtree: don't select it
          // (meaning, return false). In other words, return the result of
          // asking if the node list is empty.
          return nl.size() == 0;
        }
      }
      // The node is not under the target root: don't select it (meaning,
      // return false).
      return false;
    }
  }

  private static class BooleanData {
    private boolean hlFld_TitleSPAN = false;
    private boolean hlFld_TitleDIV = false;
    private boolean isLikelyAbs = false;
    private boolean hasPreview = false;
  }

  @Override
  public InputStream createFilteredInputStream(final ArchivalUnit au,
                                                            InputStream in,
                                                            String encoding)
      throws PluginException {

    final BooleanData bD = new BooleanData();

    /* make nodefilters of includables */
    NodeFilter[] commonIncludes = new NodeFilter[] {
        // KEEP top part of main content area [TOC, abs, full, ref]
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "overview"),
        // KEEP each article block [TOC]
        //need to keep the second \\b so we don't pick up articleMetrics, or articleTools
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "\\barticle\\b[^-_]"), // avoid match on pageArticle, article-card
        // KEEP abstract [abs, full, ref]
        /// removing this, it is too broad the string 'abstract' occurs in many div classes - markom 4/7/2021
        //HtmlNodeFilters.tagWithAttributeRegex("div", "class", "abstract"),
        // KEEP active content area [abs, full, ref, suppl]
        /// This Div contained a lot of info that was not reproduced in the new 'theme'
        // Nothing to do but not include it. - markom 4/17/2021
        //HtmlNodeFilters.tagWithAttribute("div", "id", "informationPanel"), // article info [abs]
        HtmlNodeFilters.tagWithAttribute("div", "id", "fulltextPanel"), // full text [full]
        HtmlNodeFilters.tagWithAttribute("div", "id", "referencesPanel"), // references [ref]
        //HtmlNodeFilters.tagWithAttribute("div", "id", "supplementaryPanel"), // supplementary materials [suppl]
        //HtmlNodeFilters.tagWithAttribute("div", "class", "figuresContent"), //doi/figures/...
        // KEEP citation format form [showCitFormats]
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "citationContainer"),
        //HtmlNodeFilters.tagWithAttributeRegex("div", "class", "citationFormats"),
        // KEEP popup window content area [showPopup]
        HtmlNodeFilters.tagWithAttribute("body", "class", "popupBody"),
        // New skin 2017 - re-examining all aspects from scratch
        // TOC (now Regex, to catch class= with multiple entries
        //HtmlNodeFilters.tagWithAttributeRegex("div", "class","tocArticleEntry"),
        // Abstract
        // Full text html - used doi/(full|abs)/10.1080/01650424.2016.1167222
        /* Do not include this anymore, we will grab title from elsewhere -- markom April 6, 2021
        HtmlNodeFilters.tagWithAttribute("div", "class","publicationContentTitle"),

        HtmlNodeFilters.tagWithAttributeRegex("div", "class","abstractSection "),
        */
        // likewise, this is too broad, grab the child divs instead, doing this lower in '//new content...' section
        // HtmlNodeFilters.tagWithAttribute("div", "class","hlFld-Fulltext"),
        // Figures page (may or may not have contents
        HtmlNodeFilters.tagWithAttribute("div","class","figuresContent"),
        // showCitFormats form page
        // This tag doesnt include the article info, only Download options and formats -- markom April 28, 2021
        //HtmlNodeFilters.tagWithAttribute("div","class","downloadCitation"),
        // This tag is for ONLY the citation itself for New template of website. -- markom April 28, 2021
        HtmlNodeFilters.tagWithAttribute("article","class","searchResultItem"),
        // an article with suppl and in-line video plus zip
        //doi/suppl/10.1080/11263504.2013.877535
        //and one with multiple downloadable files
        //doi/suppl/10.1080/1070289X.2013.822381
        // HtmlNodeFilters.tagWithAttribute("div","class", "supplemental-material-container"),

        // older content that we need on the ingest machines
        //HtmlNodeFilters.allExceptSubtree(
        //    HtmlNodeFilters.tagWithAttributeRegex("ul", "class", "references"),
        //    HtmlNodeFilters.tag("strong")
        //),

        // new content that we need to include to compare with older content, also, just like to keep this content
        //HtmlNodeFilters.tagWithAttribute("div", "id", "references-Section"),
        // grab the title of the article, there are too many ways they do this, but these three filters work so far.
        //HtmlNodeFilters.tagWithAttribute("div","class","description"), // we only grab child h1 tags below

        // We can't even grab the author names because of the inconsistent placement, or formatting.
        // grab the author names, but exclude the email and affiliation
//            HtmlNodeFilters.allExceptSubtree(
//                HtmlNodeFilters.tagWithAttribute("a","class","entryAuthor"),
//                HtmlNodeFilters.tag("span")
//            ),
        // there is a div.class.hlFld-Abstract parent of a hlFld-Abstract child, causing duplicate abstract text,
        //HtmlNodeFilters.tagWithAttributeRegex("div", "class","hlFld-Abstract"),
        // hopefully grabbing the inner will work
        HtmlNodeFilters.tagWithAttributeRegex("div", "class","abstractSection"),

        /// This commented out section is needed to get the Abstract text from Cancer Biology & Therapy /abs/ pages
        //,
//            HtmlNodeFilters.tagWithAttributeRegex("div", "class","abstract module ") {
        /*new HtmlNodeFilters() {
          String jId = au.getTdbAu().getAttr("journal_id");
          if (jId.equals("kcbt20")) {
            HtmlNodeFilters.tagWithAttributeRegex("div", "class","abstract module ");
          }
        },
        */
        // the abstract is sometimes inside of a p tag with class = 'first last'
        //HtmlNodeFilters.tagWithAttribute("p", "class","first last"),
        // HtmlNodeFilters.tagWithAttributeRegex("div", "class","abstract module"),
        HtmlNodeFilters.tagWithAttributeRegex("div", "class","hlFld-KeywordText"),
        // keep Ack[nowledgments] section
        // Note, sometimes the acknowledgemetns does not have this div
        // and is instead in a more generic NLM_sec_level_1 class
        HtmlNodeFilters.tagWithAttribute("div", "id", "ack"),
        // We only want the div class="ack" that is child of hlFld-fulltext

        // keep on new content from issue TOC
        // some children of these get removed below
        HtmlNodeFilters.tagWithAttributeRegex("div", "class","tocAuthors"),
        /*
        HtmlNodeFilters.tagWithAttribute("a", "class","expander open"),
        HtmlNodeFilters.tagWithAttribute("div", "class","yearContent open"),
        */
        // we want the value of this tag <input type="hidden" name="title" value="Alcheringa: An Australasian Journal of Palaeontology (2014)">
        // sadly, not sure how to do so

        // we get rid of all tags at the end so won't keep links unless explicitly
        // included here
        // This includes the links on a manifest page
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (node instanceof LinkTag) {
              String link = ((LinkTag) node).getAttribute("href");
              if(link != null && !link.isEmpty() && link.matches("^(https?://[^/]+)?/toc/[^/]+/[^/]+/[^/]+/?$")) {
                Node parent = node.getParent().getParent();
                if(parent instanceof BulletList) {
                  if(parent.getParent() instanceof BodyTag) {
                    return true;
                  }
                }
              }
              // the a tag for entryTitle on old content has an h3 child,
              // this is a bonkers situation, but there is an h3 tag we get rid of elsewhere that also gets rid of
              // this one, so lets change it to a p tag. :P - markom 4/9/2021
              String tagClass = ((LinkTag) node).getAttribute("class");
              if(tagClass != null && !tagClass.isEmpty() && tagClass.equals("entryTitle")) {
                Node child = node.getFirstChild();
                if (child instanceof HeadingTag) {
                  //log.info(((HeadingTag) child).getStringText());
                  ((HeadingTag) child).setTagName("p");
                }
              }

            }
            return false;
          }
        },
        // This checks if the parent of a div class="ack" is class="hlFld-Fulltext" and accepts if it is, otherwise, it
        // is LIKELY already included from "fulltextPanel"
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (node instanceof Div) {
              String divClass = ((Div) node).getAttribute("class");
              if(divClass != null && !divClass.isEmpty() && divClass.equals("ack")) {
                Node parent = node.getParent();
                if (node instanceof Div) {
                  String parentClass = ((Div) parent).getAttribute("class");
                  if(parentClass != null && !parentClass.isEmpty() && parentClass.equals("hlFld-Fulltext")) {
                    return true;
                  }
                }
              }
            }
            return false;
          }
        },
        // This checks if the grandparent|parent of div.NLM_sec-type_appendix is div class="back" and does not accept it
        // if it is not an ancestor it accepts it
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            return tagWithAttrNotDescendantWithAttr(
                node,"class", "NLM_sec-type_appendix",
                "class",  "back" );
          }
        },
        // check if div.summation-section is the ancestor of hlFld-FullText
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            return tagWithAttrNotDescendantWithAttr(
                node, "class", "summation-section",
                "class",  "hlFld-FullText" );
          }
        },

        // HtmlNodeFilters.tagWithAttributeRegex("div", "class","NLM_sec_level_1"),
        // check if div.NLM_sec_level_1 is the ancestor of a .ack and discards, otherwise keeps.
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            return tagWithAttrNotDescendantWithAttr(
                node, "class", "NLM_sec_level_1",
                "class",  "ack",
                "class",  "author-infos" );
          }
        },
        // Next two NodeFilters() replace this pair that would often result in duplicates
        //HtmlNodeFilters.tagWithAttribute("div","class","hlFld-Title"), // we only grab child h1 tags below
        //HtmlNodeFilters.tagWithAttributeRegex("span","class","hlFld-Title", true),
        // html.pb-page body.pb-ui div#pb-page-content div div#<hash>.widget.pageBody.none.widget-none.widget-compact-all div.wrapped div.widget-body.body.body-none.body-compact-all div.page-body.pagefulltext div
        //     div#<hash>.widget.responsive-layout.none.publicationContentBody.widget-none                       div.wrapped  div.widget-body.body.body-none                  div.container  div.row.row-md  div.col-md-7-12 div.contents  div#<hash>.widget.literatumPublicationContentWidget.none.widget-none.widget-compact-all                    div.wrapped  div.widget-body.body.body-none.body-compact-all  div.articleMeta.ja  div.hlFld-Title  div.publicationContentTitle  h1.chaptertitle
        //     div#<hash>.widget.responsive-layout.none.publicationContentHeader.widget-none.widget-compact-all  div.wrapped  div.widget-body.body.body-none.body-compact-all div.container  div.row.row-md  div.col-md-2-3  div.contents  div#<hash>.widget.literatumPublicationHeader.none.literatumPublicationTitle.widget-none.widget-compact-all div.wrapped  div.widget-body.body.body-none.body-compact-all  h1                  span.NLM_article-title.hlFld-title
        // Div.hlFld_title checker
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (node instanceof Div) {
              String divClass = ((Div) node).getAttribute("class");
              if (divClass != null && !divClass.isEmpty() && divClass.equalsIgnoreCase("hlfld-title")) {
                // check if the other version of the title has been saved already
                if (!bD.hlFld_TitleSPAN) {
                  bD.hlFld_TitleDIV = true;
                return true;
                }
              }
            }
            return false;
          }
        },
        // Span.hlFld_title checker
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (node instanceof Span) {
              String spanClass = ((Span) node).getAttribute("class");
              if (spanClass != null && !spanClass.isEmpty()) {
                // check if the other version of the title has been saved already
                if (!bD.hlFld_TitleDIV && spanClass.equalsIgnoreCase("hlfld-title")) {
                    bD.hlFld_TitleSPAN = true;
                  return true;
                }
              }
            }
            return false;
          }
        },
        new NodeFilter() {
          @Override
          public boolean accept (Node node) {
            if (node instanceof Html) {
              // set variables
              // this is needed because it seems that the variable gets saved between documents.
              log.debug3(":Html (i.e. beginning of filtering):");
              log.debug3("  isLikelyAbs:     " + bD.isLikelyAbs);
              log.debug3("  hasPreview:      " + bD.hasPreview);
              log.debug3("  hlFld_TitleDIV:  " + bD.hlFld_TitleDIV);
              log.debug3("  hlFld_TitleSPAN: " + bD.hlFld_TitleSPAN);
              //hlFld_TitleSPAN = false;
              //hlFld_TitleDIV = false;
              //isLikelyAbs = false;
              //hasPreview = false;
            } else if (node instanceof HtmlTags.Footer) {
              log.debug3(":Footer (i.e. end of filtering):");
              log.debug3("  isLikelyAbs:     " + bD.isLikelyAbs);
              log.debug3("  hasPreview:      " + bD.hasPreview);
              log.debug3("  hlFld_TitleDIV:  " + bD.hlFld_TitleDIV);
              log.debug3("  hlFld_TitleSPAN: " + bD.hlFld_TitleSPAN);
            }
            return false;
          }
        },

        // complicated abstract deduplicator
        new NodeFilter() {
          // html body.script div#doc.pageArticle div#bd div.section div#tandf_content div.clear div#journal_content div#pubContentMath.hideMathJax div#unit2.unit
          //   ...CHILDREN...
          //   path to one type of abstract / div.abstract.module.borderedmodule-last -> div.bd -> div.gutter -> (div.paragraph || (p.first & p.last))
          //   span tag, unimportant        / span#tabs-option.off-screen
          //   path to most of article      / div#tabModule.summations.module -> div.gutter -> div.tabs.clear.tabGutter
          //     ...CHILDREN...
          //     menu bar, unimportant                                   / ul.tabsNav
          //     author bios, article published date, etc., unimportant  / div#informationPanel ...
          //     full text section, this contains the other abstract     / div#fulltextPanel.tabsPanel -> div.gutter.gutterSec -> div#abstract.summationHeading.clear.clearfix
          //     this tab is the sibling to check if figures is active   / div#figuresTablesPanel -> div.gutter.gutterSec ...
          @Override
          public boolean accept(Node node) {
            boolean returnAble = false;
            boolean isPotentialAbstractText = false;
            String nodeClass = null;
            if (node instanceof Div) {
              nodeClass = ((Div) node).getAttribute("class");
              isPotentialAbstractText = nodeClass != null && !nodeClass.isEmpty() && nodeClass.equals("paragraph");
            } else if (node instanceof ParagraphTag) {
              // nodeClass = ((ParagraphTag) node).getAttribute("class");
              // used to check class value, but sometimes there is no class! so just check all p tags :P -- markom 4/30/2021
              isPotentialAbstractText = true;
              //isPotentialAbstractText = nodeClass != null && !nodeClass.isEmpty() && (
              //    nodeClass.equals("first") || nodeClass.equals("last") || nodeClass.equals("first last")
              //);
            }
            if (isPotentialAbstractText) {
              Node parent = node.getParent();
              if (parent instanceof Div) {
                Node grandParent = parent.getParent();
                if (grandParent instanceof Div) {
                  Node greatGrandParent = grandParent.getParent();
                  if (greatGrandParent instanceof Div) {
                    String greatGrandParentClass = ((Div) greatGrandParent).getAttribute("class");
                    if (greatGrandParentClass != null && !greatGrandParentClass.isEmpty() && greatGrandParentClass.contains("abstract module")) {
                      returnAble = true; // we found an abstract, set return to true, unless further checks are true
                      // If we find a <div id="preview" ... we can assume this is an '/abs/' containing url and can return
                      // without further checks.
                      Node previewDiv = findSiblingDivByAttr(greatGrandParent, "id", "preview");
                      if (previewDiv != null) {
                        bD.isLikelyAbs = returnAble;
                        return returnAble;
                      }
                      // find the sibling that id div#tabModule, note the structure shown at top of this filter
                      Node tabModule = findSiblingDivByAttr(greatGrandParent, "id", "tabModule");
                      if (tabModule != null) { // there is '\n' any number of times, then div.gutter
                        Node tabModuleChild = findSiblingDivByAttr(tabModule.getFirstChild(), "class", "gutter");
                        if (tabModuleChild != null) {// ibid... then div.tabs.clear.tabGutter
                          Node tabModuleGrandChild = findSiblingDivByAttr(tabModuleChild.getFirstChild(), "class", "tab"); // tabs | tabGutter
                          if (tabModuleGrandChild != null) {// '\n' then ul.tabsNav then '\n' then div#informationPanel '\n' then div#fullTextPanel
                            Node fullTextPanel = findSiblingDivByAttr(tabModuleGrandChild.getFirstChild(), "id", "fulltextPanel");
                            if (fullTextPanel != null) { // will not be present if not /full/ url, typically
                              Node fullTextPanelChild = findSiblingDivByAttr(fullTextPanel.getFirstChild(), "class", "gutter");
                              if (fullTextPanelChild != null) {
                                Node abstractDiv = findSiblingDivByAttr(fullTextPanelChild.getFirstChild(), "id", "abstract");
                                if (abstractDiv != null) {
                                  // we found another abstract, do not return this one
                                  returnAble = false;
                                }
                              }
                            }
                            // another check is to make sure that the figuresTablesPanel div is empty or not
                            // if it is not empty, we should discard the abstract
                            if (isSiblingDivWAttrEmpty(tabModuleGrandChild.getFirstChild(),  "id",  "figuresTablesPanel")) {
                              returnAble = false;
                            }
                            // ibid, but for referencesPanel
                            if (isSiblingDivWAttrEmpty(tabModuleGrandChild.getFirstChild(),  "id",  "referencesPanel")) {
                              returnAble = false;
                            }
                            // ibid for supplementaryPanel
                            if (isSiblingDivWAttrEmpty(tabModuleGrandChild.getFirstChild(),  "id",  "supplementaryPanel")) {
                              returnAble = false;
                            }
                            bD.isLikelyAbs = returnAble;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
            return returnAble;
          }
        },
        // if we find a <div id="preview" ..  then we set assumption of abstract page.
        // this boolean gets checked in the excludes and removes 'fullTextPanel' if so.
        // this is a necessary thing because infrequently there is content in 'fullTextPanel'
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (node instanceof Div) {
              String divId = ((Div) node).getAttribute("id");
              if (divId != null && !divId.isEmpty() &&
                  (divId.contains("preview") || divId.contains("firstPage"))
              ) {
                // set likely abstract to true.
                bD.hasPreview = true;
              }
            }
            return false;
          }
        },
    };

    /* make a nodefilter of excludables */
    NodeFilter[] commonExcludes = new NodeFilter[] {
        // DROP scripts, styles, comments
        HtmlNodeFilters.tag("script"),
        HtmlNodeFilters.tag("noscript"),
        HtmlNodeFilters.tag("style"),
        HtmlNodeFilters.comment(),
        // DROP social media bar [overview]
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "social"),
        // DROP access box (changes e.g. when the article becomes free) [article block, abs/full/ref/suppl overview]
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "accessmodule"),
        HtmlNodeFilters.tagWithAttribute("div", "class", "access"), // formerly by itself
        // DROP number of article views [article block, abs/full/ref/suppl overview]
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "articleUsage"),
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "literatumArticleMetricsWidget"),
        // DROP "Related articles" variants [article block, abs/full/ref/suppl overview]
        HtmlNodeFilters.tagWithAttributeRegex("a", "class", "relatedLink"), // old?
        HtmlNodeFilters.tagWithAttributeRegex("li", "class", "relatedArticleLink"), // [article block]
        HtmlNodeFilters.tagWithText("h3", "Related articles"), // [abs/full/ref/suppl overview]
        HtmlNodeFilters.tagWithAttributeRegex("a", "class", "searchRelatedLink"), // [abs/full/ref/suppl overview]
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "combinedRecommendationsWidget"), // all - "People also read"
        // DROP title options (e.g. 'Publication History', 'Sample this title') [TOC overview]
        HtmlNodeFilters.tagWithAttribute("div", "class", "options"),
        // DROP title icons (e.g. 'Routledge Open Select') [TOC overview]
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "journalOverviewAds"),
        // DROP book review subtitle (added later)
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "subtitle"), // [TOC] e.g. http://www.tandfonline.com/toc/wtsw20/33/1)
        // ...placeholder for [abs/full/ref/suppl overview] e.g. http://www.tandfonline.com/doi/full/10.1080/08841233.2013.751003
        // DROP Google Translate artifacts [abs/full/ref/suppl overview]
        HtmlNodeFilters.tagWithAttribute("div", "id", "google_translate_element"), // current
        HtmlNodeFilters.tagWithAttribute("div", "id", "goog-gt-tt"), // old
        HtmlNodeFilters.tagWithText("a", "Translator disclaimer"),
        HtmlNodeFilters.tagWithText("a", "Translator&nbsp;disclaimer"),
        // DROP "Alert me" variants [abs/full/ref overview]
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "alertDiv"), // current
        HtmlNodeFilters.tagWithAttributeRegex("div", "id", "alertDiv"), // old
        // DROP "Publishing models and article dates explained" link [abs/full/ref/suppl overview]
        HtmlNodeFilters.tagWithAttributeRegex("a", "href", "models-and-dates-explained"),
        // DROP article dates which sometimes get fixed later [abs/full/ref/suppl overview]
        HtmlNodeFilters.tagWithAttribute("div", "class", "articleDates"),
        // DROP subtitle for journal section/subject (added later) [abs/full/ref/suppl overview]
        HtmlNodeFilters.tagWithAttribute("span", "class", "subj-group"),
        // DROP non-access box article links (e.g. "View full text"->"Full text HTML") [abs/full/ref/suppl overview]
        HtmlNodeFilters.tagWithAttributeRegex("ul", "class", "top_article_links"),
        // DROP outgoing links and SFX links [article block, full, ref, probably showPopup]
        //HtmlNodeFilters.allExceptSubtree(
        //      HtmlNodeFilters.tagWithAttribute("span", "class", "referenceDiv"),
        //      HtmlNodeFilters.tagWithAttribute("a", "class", "dropDownLabel")
        //  ), // popup at each inline citation [full]
        HtmlNodeFilters.tagWithAttributeRegex("a", "href", "^/servlet/linkout\\?"), // [article block, full/ref referencesPanel]
        HtmlNodeFilters.tagWithAttribute("a", "class", "sfxLink"), // [article block, full/ref referencesPanel]
        HtmlNodeFilters.tagWithAttributeRegex("a", "href", "javascript:newWindow\\('http://dx.doi.org/"), // [showPopup, probably article block, full/ref referencesPanel]
        // DROP "Jump to section" popup menus [full]
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "summationNavigation"),
        HtmlNodeFilters.tagWithAttributeRegex("a", "title", "(Next|Previous) issue"),
        HtmlNodeFilters.tagWithAttributeRegex("div", "id", "breadcrumb"),
        //descriptive text that often changes
        HtmlNodeFilters.tagWithAttribute("td", "class", "note"),

        //TOC
        HtmlNodeFilters.tagWithAttribute("div", "class", "sfxLinkButton"),
        //Abstract
        //Full
        HtmlNodeFilters.tagWithAttribute("span", "class","ref-lnk"), //in-line rollover ref info
        //Figures
        //showCit
        // showCit can only include the title now, see this old format of the citation compared to new version
        /* / Note the absence of the word 'and' before the final author in the old version but not in the new.
           / also note the lack of a space between the comma and '2015' in the new version but not the old.
        <li>
          <div class="art_title"><a href="/doi/abs/10.2989/00306525.2015.1030793">A novel methodology for the rapid assessment of waterbird vulnerability to disturbance</a></div>
            Kate JH England
            , Colin Jackson
            , Philip AR Hockey
          <a href="http://www.tandfonline.com/toc/tost20/86/1-2">
            <span class="journalName">Ostrich</span>
          </a>
            Vol. <span class="volume">86</span>,
            Iss. <span class="issue">1-2</span>,
                 <span class="year">2015</span>
        </li>
        // New version
        <article class="searchResultItem">
          <div class="art_title"><a href="/doi/abs/10.2989/00306525.2015.1030793" class="ref nowrap">A novel methodology for the rapid assessment of waterbird vulnerability to disturbance</a></div>
          <div class="author"><div class="art_authors"><span class="entryAuthor">Kate JH England, Colin Jackson, and Philip AR Hockey</span></div> </div>
          <div class="publication-meta">
              <a class="issue-link" href="/toc/tost20/86/1-2">
                  <span class="journalName">Ostrich</span>
              </a>
              <span class="volume">Vol. 86 , Iss. 1-2,2015 </span>
          </div>
        </article>
         */
        HtmlNodeFilters.allExceptSubtree(
            HtmlNodeFilters.tagWithAttributeRegex("div", "class", "citationContainer"),
            // can be a div, or a span, so just check class value.
            new HtmlNodeFilters.HasAttributeRegexFilter("class", "art_title", true)
        ),
        HtmlNodeFilters.allExceptSubtree(
            HtmlNodeFilters.tagWithAttribute("article","class","searchResultItem"),
            // can be a div, or a span, so just check class value.
            new HtmlNodeFilters.HasAttributeRegexFilter("class", "art_title", true)
        ),
        /*
         * Noticed changes in 10/2/2018 - views counts on TOC
         */
        HtmlNodeFilters.tagWithAttributeRegex("span", "class", "access-icon"),
        HtmlNodeFilters.tagWithAttribute("div", "class", "metrics-panel"),

        // FOR ARTICLE PAGES
        // older content that we do not need on the ingest machines
        // many title tags we need

        HtmlNodeFilters.allExceptSubtree(
            HtmlNodeFilters.tag("h3"),
            // in new content there is a table caption embedded in an h3 tag. wild
            HtmlNodeFilters.tag("p")
        ),
        HtmlNodeFilters.allExceptSubtree(
            HtmlNodeFilters.tagWithAttribute("div","class","description"),
            HtmlNodeFilters.tag("h1")
        ),
        HtmlNodeFilters.allExceptSubtree(
            HtmlNodeFilters.tagWithAttributeRegex("div","class","hlFld-Title", true),
            HtmlNodeFilters.tag("h1") // ...WithAttribute("div", "class", "publicationContentTitle")
        ),
        // a note that appears on new article pages that tells us the paper was presented at a conference
        HtmlNodeFilters.tagWithAttribute("span","class","ref-overlay scrollable-ref"),
        // ancillary author info
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "hlFld-ContribAuthor"),
        // Some ancillary author info is sometimes outside the 'hlFld-ContribAuthor' tag, it is in 'NLM-author-notes'
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "NLM_author-notes"),
        // Keywords are inconsistent too, we need to explicitly exclude them as sometimes they are embedded in a
        // kept tag
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "abstractKeywords"),
        HtmlNodeFilters.tagWithAttributeRegex("ul", "class", "keywords"),
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "hlFld-KeywordText"),
        // and another way of presenting keywords!
        HtmlNodeFilters.tagWithAttribute("table","class","NLM_def-list"),

        // all the ways of having headings
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "summationHeading"),
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "sectionHeading"),
        HtmlNodeFilters.tagWithAttributeRegex("p", "class", "summary-title"),
        HtmlNodeFilters.tagWithAttribute("div", "class", "sectionHeadingDiv"),
        // some lists have numbers in the old theme, new theme replaces with 'bullet' style list
        HtmlNodeFilters.tagWithAttribute("td", "class", "list-td"),
        // embedded reference popups.
        HtmlNodeFilters.tagWithAttribute("span", "class", "referenceDiv"),
        HtmlNodeFilters.tagWithAttributeRegex("span", "class", "ref-lnk"),// lazy-ref"),
        HtmlNodeFilters.tagWithAttribute("div", "class", "ref-lnk lazy-ref"),
        HtmlNodeFilters.tagWithAttribute("span", "class", "ref-lnk fn-ref-lnk"),
        HtmlNodeFilters.tagWithAttribute("div", "class", "ref-lnk fn-ref-lnk"),
        HtmlNodeFilters.tagWithAttribute("ul", "class", "references"),
        HtmlNodeFilters.tagWithAttribute("div", "class", "pageRange"),
        HtmlNodeFilters.tagWithAttribute("div", "class", "doiMeta clear"),
        // title and author info is embedded in the figures and tables (but with differing format, exclude
        // figureViewerArticleInfo, tableViewerArticleInfo, for the new content this is a class name
        HtmlNodeFilters.tagWithAttributeRegex("div", "id", "ViewerArticleInfo" ),
        // old crawls have the tables at the bottom.
        HtmlNodeFilters.tagWithAttributeRegex("div", "id", "table-content-" ),
        // There are class=hlFld-Title tags in the related articles section that get pulled in that we do not want.
        // this is sufficient in place of the div and span checks. thank goodness.
        HtmlNodeFilters.ancestor(
            HtmlNodeFilters.tagWithAttribute("div", "class", "relatedItem")
        ),

        // this is for both new and old crawls,
        // figureDownloadOption tableDownloadOption
        // old crawls have 'PowerPoint...' new crawls 'Display...'
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "DownloadOption" ),

        // newer content that we need to exclude to agree with older content
        // We would like to keep the references, but the new theme does not embed the list numbers while the old theme does
        HtmlNodeFilters.tagWithAttributeRegex("h2", "id", "."),
        HtmlNodeFilters.tagWithAttributeRegex("h2", "class", "."),
        // the Acknowledgments title is in a span class=title not h[/d]
        HtmlNodeFilters.tagWithAttribute("span", "class", "title"),
        HtmlNodeFilters.tagWithAttribute("p", "class", "kwd-title"),
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "ViewerArticleInfo" ),
        HtmlNodeFilters.tagWithAttributeRegex("div", "class", "alertDiv"),

        // ISSUE TOC PAGES
        // OLD CONTENT
        HtmlNodeFilters.tagWithAttribute("div", "class","overview borderedmodule-last"),
        HtmlNodeFilters.tagWithAttributeRegex("ul", "class","doimetalist"),
        // h4 filtering takes place at bottom
        HtmlNodeFilters.tag("h5"),
        HtmlNodeFilters.allExceptSubtree(
            HtmlNodeFilters.tagWithAttribute("span", "class", "articleEntryAuthorsLinks"),
            // in new content there is a table caption embedded in an h3 tag. wild
            HtmlNodeFilters.tag("a")
        ),
        HtmlNodeFilters.tagWithAttribute("div", "class","ft"),
        HtmlNodeFilters.tagWithAttribute("div", "class","article-type"),
        // NEW CONTENT
        HtmlNodeFilters.tagWithAttribute("div", "class","article-type"),
        //HtmlNodeFilters.tagWithAttributeRegex("div", "class","art_title"),
        HtmlNodeFilters.tagWithAttributeRegex("div", "class","tocPageRange"),
        HtmlNodeFilters.tagWithAttribute("div", "class","tocDeliverFormatsLinks"),
        HtmlNodeFilters.tagWithAttribute("div", "class","tocEPubDate"),

        // appendix section that is not regular
        // NOTE: added 'complicated' NodeFilter to commonIncludes, should now be including these
        //HtmlNodeFilters.tagWithAttribute("div", "class", "back"),
        //HtmlNodeFilters.tagWithAttributeRegex("div", "class", "NLM_sec-type_appendix"),

        // suppplemental section that is embedded on some pages but not most
        HtmlNodeFilters.tagWithAttribute("div", "id", "supplemental-material-section"),

        //// Node removal that was (and may again) be useful, but was taken care of with more discretionary Includes
        /* A informational section that is usually discarded, but sometimes has ambiguous tag names/classes
        // search for the 'header' words and get rid of the whole div.
        //HtmlNodeFilters.tagWithTextRegex("div", "Details[\\s\\S]*Received:[\\S\\s]*Accepted:"),*/
        /* HtmlNodeFilters.tagWithAttribute("span", "class", "googleScholar-container"),*/
        /*HtmlNodeFilters.allExceptSubtree(
          HtmlNodeFilters.tagWithAttribute("div", "class", "doiMeta clear"),
          HtmlNodeFilters.tagWithAttribute("span", "class", "hlFld-ContribAuthor")
        ),*/
        /* New skin 2017 - exclusion based on new includes
         the following are brought in by regex "\\barticle[^-]"
         which needs to stay in for old/gln content...work around
        HtmlNodeFilters.tagWithAttribute("div", "class","articleMetricsContainer"), // brought in by the regex "\\barticle[^-]"
        HtmlNodeFilters.tagWithAttribute("div", "class", "articleTools"), */
        /*HtmlNodeFilters.tagWithAttribute("span", "class","slider-vol-year"),
        HtmlNodeFilters.tagWithAttribute("a", "class",""),*/

        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (node instanceof Span) {
              Span span = ((Span) node);
              for(Node child:span.getChildrenAsNodeArray()) {
                if (child != null && child instanceof LinkTag) {
                  String title = ((LinkTag) child).getAttribute("title");
                  if (title != null && !title.isEmpty() && title.contains("Previous issue")) {
                    return true;
                  }
                }
              }
            }
            return false;
          }
        },

        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (node instanceof Div) {
              Div div = ((Div) node);
              String divClass = div.getAttribute("class");
              if(divClass != null && !divClass.isEmpty() && divClass.contains("right")) {
                Node parent = div.getParent();
                if (parent != null && parent instanceof Div) {
                  String parentClass = ((Div) parent).getAttribute("class");
                  if (parentClass != null && !parentClass.isEmpty() && parentClass.contains("bodyFooterContent")) {
                    return true;
                  }
                }
              }
            }
            return false;
          }
        },
        new allExceptAnySubtree(
            HtmlNodeFilters.tag("h4"),
            new NodeFilter[] {
                HtmlNodeFilters.tag("a"), HtmlNodeFilters.tag("span"),
            }
        ),
        // remove nasty notes section that appears in summationSection
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (node instanceof Div) {
              Div div = ((Div) node);
              String divClass = div.getAttribute("class");
              if(divClass != null && !divClass.isEmpty() && divClass.contains("summationSection")) {
                String summationText = node.toString();
                if (summationText.contains("Notes:") && summationText.contains("Note:")) {
                  return true;
                }
              }
            }
            return false;
          }
        },
        // Removes rarely seen copyright disclaimer below abstract, in  <div style="..." > tag.
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (node instanceof Div) {
              Div div = ((Div) node);
              Node divParent = div.getParent();
              if(divParent != null && divParent instanceof Div) {
                Node divGrandParent = divParent.getParent();
                if(divGrandParent != null && divGrandParent instanceof Div) {
                  String gpId = ((Div) divGrandParent).getAttribute("id");
                  if (gpId != null && !gpId.isEmpty() && gpId.equals("overview")) {
                    String divText = div.toString();
                    if (divText != null && divText.contains("Additional license information")) {
                      return true;
                    }
                  }
                }
              }
            }
            return false;
          }
        },
        // if we are abstract remove full text
        new NodeFilter() {
          @Override
          public boolean accept(Node node) {
            if (bD.isLikelyAbs) {
              if (node instanceof Div) {
                Div div = ((Div) node);
                String divID = div.getAttribute("id");
                if(divID != null && !divID.isEmpty() && divID.contains("fulltextPanel")) {
                  return true;
                }
              }
            }
            return false;
          }
        },
    };

    /* combine the filters */

    HtmlFilterInputStream filtered = new HtmlFilterInputStream(
      in,
      encoding,
      new HtmlCompoundTransform(
        // KEEP: throw out everything but main content areas
        HtmlNodeFilterTransform.include(new OrFilter(commonIncludes)),
        // DROP: filter remaining content areas
        HtmlNodeFilterTransform.exclude(new OrFilter(commonExcludes))
    ));

    Reader reader = FilterUtil.getReader(filtered, encoding);

    LineRewritingReader rewritingReader = new LineRewritingReader(reader) {
      @Override
      public String rewriteLine(String line) {
        // Markup changes over time [anywhere]
        line = PAT_NBSP.matcher(line).replaceAll(REP_NBSP);
        line = PAT_AMP.matcher(line).replaceAll(REP_AMP);
        line = PAT_PUNCTUATION.matcher(line).replaceAll(REP_PUNCTUATION); // e.g. \(, \-, during encoding glitch (or similar)
        // Alternate forms of citation links [article block]
        line = PAT_CITING_ARTICLES.matcher(line).replaceAll(REP_CITING_ARTICLES);
        // Wording change over time, and publication dates get fixed much later [article block, abs/full/ref/suppl overview]
        // For older versions with plain text instead of <div class="articleDates">
        line = PAT_PUBLISHED_ONLINE.matcher(line).replaceAll(REP_PUBLISHED_ONLINE);
        // Leftover commas after outgoing/SFX links removed [full/ref referencesPanel]
        line = PAT_PUB_ID.matcher(line).replaceAll(REP_PUB_ID);
        return line;
      }
    };

    // Remove all inner tag content
    Reader noTagFilter = new HtmlTagFilter(
        new StringFilter(rewritingReader, "<", " <"),
        new TagPair("<", ">")
    );
    
    // Remove white space
    Reader noWhiteSpace = new WhiteSpaceFilter(noTagFilter);
    
    InputStream ret = new ReaderInputStream(noWhiteSpace);

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

  public static final String EMPTY_STRING = "";

  public static final Pattern PAT_NBSP = Pattern.compile("&nbsp;", Pattern.CASE_INSENSITIVE);
  public static final String REP_NBSP = " ";
  
  public static final Pattern PAT_AMP = Pattern.compile("&amp;", Pattern.CASE_INSENSITIVE);
  public static final String REP_AMP = "&";
  
  public static final Pattern PAT_PUNCTUATION = Pattern.compile("[,\\\\]", Pattern.CASE_INSENSITIVE);
  public static final String REP_PUNCTUATION = EMPTY_STRING;
  
  public static final Pattern PAT_CITING_ARTICLES = Pattern.compile("<li>(<div>)?(<strong>)?(Citing Articles:|Citations:|Citation information:|<a href=\"/doi/citedby/).*?</li>", Pattern.CASE_INSENSITIVE); 
  public static final String REP_CITING_ARTICLES = EMPTY_STRING;
  
  public static final Pattern PAT_PUBLISHED_ONLINE = Pattern.compile("(<(b|h[23456])>)?(Published online:|Available online:|Version of record first published:)(</\\2>)?.*?>", Pattern.CASE_INSENSITIVE);
  public static final String REP_PUBLISHED_ONLINE = EMPTY_STRING;
  
  public static final Pattern PAT_PUB_ID = Pattern.compile("</pub-id>.*?</li>", Pattern.CASE_INSENSITIVE); 
  public static final String REP_PUB_ID = EMPTY_STRING;

}
