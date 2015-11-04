/* $Id$

 Copyright (c) 2000-2003 Board of Trustees of Leland Stanford Jr. University,

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

package org.lockss.plugin.medknow;

import java.io.*;

import org.apache.commons.io.IOUtils;
import org.lockss.util.*;
import org.lockss.test.LockssTestCase;
import org.lockss.test.MockArchivalUnit;
import org.lockss.test.StringInputStream;


public class TestMedknowHtmlHashFilterFactory extends LockssTestCase {
  private MedknowHtmlHashFilterFactory fact;
  private MockArchivalUnit mau;

  public void setUp() throws Exception {
    super.setUp();
    fact = new MedknowHtmlHashFilterFactory();
  }


  private static final String citationCounts =
          "<table class=\"articlepage\" >" +
          "<font class=\"CorrsAdd\">" +
          "<img src=\"http://api.elsevier.com/content/abstract/citation-count?pubmed_id=2352505\">" +
          "<div style=\"float: left; border:1px solid #ddd;padding:4px;\">" +
          "<a href=\"crossrefCitation.asp?doi=10.4103/0022-3859.109492\" target=\"_blank\">" +
          "<table width=\"100%\">" +
          "<tbody>" +
          "<tr>" +
          "<td>" +
          "<img src=\"http://www.medknow.com\" alt=\"Crossref citations\">" +
          "</td>" +
          "<td style=\"font-size:11px;background-color:#ddd;padding:4px\" width=\"5px;\">" +
          "<b>7" +
          "</b>" +
          "</td>" +
          "</tr>" +
          "</tbody>" +
          "</table>" +
          "</a>" +
          "</div>" +
          "<div style=\"float: left; border:1px solid #ddd;padding:4px; \">" +
          "<table width=\"33%\">" +
          "<tbody>" +
          "<tr>" +
          "<td>" +
          "<img src=\"http://www.medknow.com\" alt=\"PMC citations\">" +
          "</td>" +
          "<td style=\"font-size:11px;background-color:#ddd;padding:4px\" width=\"5px\">" +
          "<a target=\"_blank\" href=\"http://www.ncbi.nlm.nih.gov\">" +
          "<b>3" +
          "</b>" +
          "</a>" +
          "</td>" +
          "</tr>" +
          "</tbody>" +
          "</table>" +
          "</div>" +
          "</font>" +
          "</table>";

  private static final String  tocHtml = 
      "<table>" +
          "<tr>" +
          "    <td width=\"90%\" colspan=\"2\" class=\"tochead\">EDITORIAL</td>" +
          "    <td width=\"10%\"  class=\"tochead\">&nbsp;</td>" +
          "  </tr>" +
          " <tr>" +
          "   <td width=\"100%\" colspan=\"3\" height=\"15px\"></td>" +
          " </tr>" +
          "  <tr>" +
          "    <td class=\"other\" vAlign=\"top\" align=\"center\" width=\"20%\" rowSpan=\"3\">" +
          "    <input type='checkbox' name='sai' value='jpgm_2015_X_Y_Z'><br></td>" +
          "    <td width=\"75%\" class=\"articleTitle\" style='margin:left:5px;'>Article Title for Editorial</td>" +
          "    <td width=\"5%\" rowspan=\"3\" class=\"other\" valign=\"top\" align=\"right\">p. 73</td>" +
          "  </tr>" +
          "  <tr>" +
          "    <td class=\"sAuthor\">P Ran, NJ Goo<br><b>DOI</b>:1X.1111/0022-3859.153101&nbsp;&nbsp;<b>PMID</b>:55555555</td>" +
          "  </tr>" +
          "  <tr>" +
          "    <td class=\"other\" style='text-align:left;'>" +
          "<a class=\"toc\" href=\"article.asp?foo\" title=\"Click to View Full Text of the article.\">[HTML Full text]</a>" +
          "&nbsp;&nbsp;" +
          "<a class=\"toc\" href=\"article.asp?foo;type=2\" title=\"Click to download PDF version of the article.\">[PDF]</a>" +
          "&nbsp;&nbsp;" +
          "<a class=\"toc\" href=\"article.asp?foo;type=3\" title=\"Click to View Full Text in Mobile format.\">[Mobile Full text]</a>" +
          "&nbsp;&nbsp;" +
          "<a class=\"toc\" href=\"article.asp?foo;type=4\" title=\"Click to download as ePub file.\">[EPub]</a>" +
          "&nbsp;&nbsp;" +
          "<a class=\"toc\" target='_blank' href='http://www.ncbi.nlm.nih.gov/pubmed/25766335'>[PubMed]</a>" +
          "&nbsp;&nbsp;" +
          "<a class=\"toc\" href=\"article.asp?foo;type=5\" title=\"Click to download Sword Plugin for Repository file.\">" +
          "[Sword Plugin for Repository]</a><sup>Beta</sup></td>" +
          "  </tr>" +
          "<tr>" +
          "    <td width=\"90%\" colspan=\"2\"  class=\"tochead\">ORIGINAL ARTICLES</td>" +
          "  </tr>" +
          "  <tr>" +
          "    <td width=\"75%\" class=\"articleTitle\" style='margin:left:5px;'>Another TItle for an Original Article</td>" +
          "  </tr>" +
          "  <tr>" +
          "    <td class=\"sAuthor\" style='line-height:18px;'>P Author, P Writer<br>" +
          "     <b>DOI</b>:1X.1111/0022-3859.150442&nbsp;&nbsp;<b>PMID</b>:66666666" +
          "    <div id='a' style='display:none;background-color:#eaeaea;border:1px solid #ddd;padding:5px;'>" +
          "       abstract goes here </div></td>" +
          "  </tr>" +
          " </table>";

  private static final String tocHtmlKept = 
      "<td width=\"90%\" colspan=\"2\" class=\"tochead\">EDITORIAL</td>" +
          "<td width=\"10%\"  class=\"tochead\">&nbsp;</td>" +
          "<td width=\"75%\" class=\"articleTitle\" style='margin:left:5px;'>Article Title for Editorial</td>" +
          "<td class=\"sAuthor\">P Ran, NJ Goo<br><b>DOI</b>:1X.1111/0022-3859.153101&nbsp;&nbsp;<b>PMID</b>:55555555</td>" +
          "<td width=\"90%\" colspan=\"2\"  class=\"tochead\">ORIGINAL ARTICLES</td>" +
          "<td width=\"75%\" class=\"articleTitle\" style='margin:left:5px;'>Another TItle for an Original Article</td>" +
          "<td class=\"sAuthor\" style='line-height:18px;'>P Author, P Writer<br>" +
          "     <b>DOI</b>:1X.1111/0022-3859.150442&nbsp;&nbsp;<b>PMID</b>:66666666" +
          "    <div id='a' style='display:none;background-color:#eaeaea;border:1px solid #ddd;padding:5px;'>" +
          "       abstract goes here </div></td>";


  private static final String articleHtml = 
      "<div><table><tr><td><table class=\"articlepage\" >" +
          "<div>"+
          "<font class=\"AuthorAff\">Foo, Blah, Blah</font>" +
          "</div>" +
          "</table></td></tr></table></div>";

  private static final String articleHtmlKept = 
      "<table class=\"articlepage\" >" +
          "<div></div>" +
          "</table>";

  private static final String onlyKept = "<table class=\"articlepage\" ></table>";

  private static final String bigTOC = "<body>" +
      "<table border=\"0\" width=\"1000\" align=\"center\"  cellspacing=\"0\" cellpadding=\"0\" >" +
      "<tr>" +
      "<td width=\"78%\" valign=\"top\" height=\"5px\">" +
      "</td>" +
      "<td width=\"2%\" valign=\"top\" height=\"5px\">" +
      "</td>" +
      "<td width=\"20%\" valign=\"top\" height=\"5px\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td valign=\"top\" class=\"empty\">   " +
      "<form name=\'saf\' action=\'srchaction.asp\' method=\'post\'>" +
      "<div id=\"exp1\" class=\"expdiv\">" +
      "<div style=\'padding:5px;border:1px solid #ddd;background-color:#eaeaea\'>" +
      "<b>Export selected to</b>" +
      "</br>" +
      "<div>" +
      "<input type=\'radio\' value=\'1\' name=\'t\'>Endnote</div>" +
      "<div>" +
      "<input type=\'radio\' value=\'2\' name=\'t\'>Reference Manager</div>" +
      "<div>" +
      "<input type=\'radio\' value=\'3\' name=\'t\'>Procite</div>" +
      "<div>" +
      "<input type=\'radio\' value=\'4\' name=\'t\'>Medlars Format</div>" +
      "<div>" +
      "<input type=\'radio\' value=\'5\' name=\'t\'>RefWorks Format</div>" +
      "<div>" +
      "<input type=\'radio\' value=\'6\' name=\'t\'>BibTex Format</div>" +
      "<div align=\'right\'>" +
      "<input type=\'submit\' name=\'Apply\' value=\'Apply\'>" +
      "</div>" +
      "</div>" +
      "</div>" +
      "&nbsp;<font class=\"pageHead\"> <img src=\"images/aboutbul.gif\" alt=\"\">&nbsp;Table of Contents </font>" +
      "<br>" +
      "<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" class=\"articlepage\">" +
      "<tr>" +
      "<td width=\'20%\' valign=\'top\' align=\'left\'>" +
      "<img alt=\'Coverpage\' border=\'0\'>" +
      "</td>" +
      "<td valign=\'top\' width=\'80%\'>" +
      "<table width=\'100%\' cellpadding=\'0\' cellspacing=\'0\' border=\'0\' class=\'body\'>" +
      "<tr>" +
      "<td valign=\'top\' width=\'50%\'>" +
      "<b>April-June&nbsp;2014</b>" +
      "<br>Volume 11&nbsp;|&nbsp;Issue 2" +
      "<br>Page Nos. 97-200<br>" +
      "<br>Online since Tuesday, May 20, 2014<br>" +
      "<br>" +
      "Accessed 33,012 times." +
      "<br>" +
      "<br>" +
      "<b>PDF access policy</b>" +
      "<br>Full text access is free in HTML pages; however the journal allows PDF access only to subscribers." +
      "<br>" +
      "<br>" +
      "<b>EPub access policy</b>" +
      "<br>reserved only for the paid subscribers." +
      "</td>" +
      "<td width=\'50%\' valign=\'top\'>" +
      "</td>" +
      "</tr>" +
      "</table>" +
      "</tr>" +
      "<tr>" +
      "<td colspan=\'3\'height=\'10px\'>" +
      "</td>" +
      "</tr>" +
      "</table>" +
      "<input type=\'hidden\' name=\'__adm\' value=\'\'>" +
      "</form>" +
      "</td> " +
      "<td width=\"2%\" valign=\"top\" class=\"empty\" >" +
      "</td>" +
      "<td width=\"20%\" valign=\"top\" class=\"empty\" >" +
      "<br>" +
      "</td>" +
      "</table>" +
  "</body>";
  
  private static String bigTOCFiltered = "";
  
  private static String bigAbs = "<body>" +
      "<table border=\"0\" width=\"1000\" align=\"center\"  cellspacing=\"0\" cellpadding=\"0\">" +
      "<tr>" +
      "<td width=\"78%\" valign=\"top\" height=\"3\" class=\"empty\">" +
      "<table border=\"0\" width=\"100%\" class=\"articlepage\">" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\" height=\"15\">" +
      "<font class=\"tocAT\">" +
      "<b>ORIGINAL ARTICLE</b>" +
      "</font>" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" height=\"5\" colspan=\"2\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">" +
      "<b>Year </b>: 2024&nbsp; |&nbsp; <b>Volume</b>" +
      ": 11&nbsp;" +
      "|&nbsp; <b>Issue</b> : 24&nbsp; |&nbsp; <b>Page</b> : 101-104</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">" +
      "<P>" +
      "<font class=\"sTitle\">Renal cell carcinoma in children and adolescence: Our experience</font>" +
      "<br>" +
      "<br>" +
      "<font class=\"articleAuthor\">" +
      "<a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>1</sup>, <a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>2</sup>, <a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>2</sup>, <a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>2</sup>, <a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>2</sup>, <a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>2</sup>" +
      "</font>" +
      "<br>" +
      "<font class=\"AuthorAff\">" +
      "<sup>1</sup>&nbsp;Department of Urology, BAR, <br/>" +
      "<sup>2</sup>&nbsp;Department of Urology, PGIMER &amp; SSKM Hospital, Kolkata, West Bengal, India<br/>" +
      "</font>" +
      "</p>" +
      "<p>" +
      "<font class=\"CorrsAdd\">" +
      "<b>Correspondence Address</b>:<br>" +
      "FOO, <br>India<br>" +
      "<a href=\'login.asp?rd=article.asp?issn=0189-6725;year=2024;volume=11;issue=24;spage=101;epage=104;aulast=FOO;type=0\'>" +
      "<img border=\'0\' src=\"http://www.medknow.com/images/email.gif\" alt=\"Login to access the Email id\">" +
      "</a>" +
      "<p>" +
      "<b>Source of Support:</b> None, <b>Conflict of Interest:</b> None</p>" +
      "<div  style=\'overflow: hidden;float:left;width:100%;padding:10px;border:0px solid #ddd;\'>" +
      "</div>" +
      "<br>" +
      "<p>" +
      "<b>DOI:</b>&nbsp;10.4103/12345<br/>" +
      "</p>" +
      "<p>" +
      "<a target=\'_blank\' href=\'http://www.copyright.com/ccc/openurl.do?sid=Medknow&issn=0189-6725&servicename=all&WT.mc_id=Medknow\'>" +
      "<img border=\'0\' src=\'http://www.medknow.com/journals/images/gp.gif\' alt=\'Get Permissions\'>" +
      "</a>" +
      "</p>" +
      "</font>" +
      "</p>" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\" height=\"10\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">" +
      "<b>Background:</b> Literature on foo </td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\" height=\"10\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">" +
      "<br>" +
      "<br>" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\" height=\"20\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">[<a href=\"article.asp?issn=0189-6725;year=2024;volume=11;issue=24;spage=101;epage=104;aulast=FOO\">FULL TEXT</a>] " +
      "[<a href=\"article.asp?issn=0189-6725;year=2024;volume=11;issue=24;spage=101;epage=104;aulast=FOO;type=2\">PDF</a>]*</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\" height=\"15\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"50%\">" +
      "<a href=\"printarticle.asp?issn=0189-6725;year=2024;volume=11;issue=24;spage=101;epage=104;aulast=FOO;type=0\" target=\"_blank\">" +
      "<img border=\"0\" src=\"templates/icon_print.gif\" alt=\"Print this article\">" +
      "</a>&nbsp;&nbsp;&nbsp;&nbsp;" +
      "<a href=\"emailArticle.asp?issn=0189-6725;year=2024;volume=11;issue=24;spage=101;epage=104;aulast=FOO;type=0\">" +
      "<img border=\"0\" src=\"templates/icon_mail.gif\" alt=\"Email this article\">" +
      "</a>" +
      "</td>" +
      "<td width=\"50%\">" +
      "</td>" +
      "</tr>" +
      "</table>" +
      "</td>" +
      "</tr>" +
      "</table>" +
      "</body>";
  
  private static String bigAbsFiltered = 
      "<table border=\"0\" width=\"100%\" class=\"articlepage\">" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\" height=\"15\">" +
      "<font class=\"tocAT\">" +
      "<b>ORIGINAL ARTICLE</b>" +
      "</font>" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" height=\"5\" colspan=\"2\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">" +
      "<b>Year </b>: 2024&nbsp; |&nbsp; <b>Volume</b>" +
      ": 11&nbsp;" +
      "|&nbsp; <b>Issue</b> : 24&nbsp; |&nbsp; <b>Page</b> : 101-104</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">" +
      "<P>" +
      "<font class=\"sTitle\">Renal cell carcinoma in children and adolescence: Our experience</font>" +
      "<br>" +
      "<br>" +
      "<font class=\"articleAuthor\">" +
      "<a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>1</sup>, <a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>2</sup>, <a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>2</sup>, <a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>2</sup>, <a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>2</sup>, <a target=\'_blank\' href=\'/searchresult.asp?search=\'>FOO</a>" +
      "<sup>2</sup>" +
      "</font>" +
      "<br>" +
      "</p>" +
      "<p>" +
      "</p>" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\" height=\"10\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">" +
      "<b>Background:</b> Literature on foo </td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\" height=\"10\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">" +
      "<br>" +
      "<br>" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\" height=\"20\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\">[<a href=\"article.asp?issn=0189-6725;year=2024;volume=11;issue=24;spage=101;epage=104;aulast=FOO\">FULL TEXT</a>] " +
      "[<a href=\"article.asp?issn=0189-6725;year=2024;volume=11;issue=24;spage=101;epage=104;aulast=FOO;type=2\">PDF</a>]*</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"100%\" colspan=\"2\" height=\"15\">" +
      "</td>" +
      "</tr>" +
      "<tr>" +
      "<td width=\"50%\">" +
      "<a href=\"printarticle.asp?issn=0189-6725;year=2024;volume=11;issue=24;spage=101;epage=104;aulast=FOO;type=0\" target=\"_blank\">" +
      "<img border=\"0\" src=\"templates/icon_print.gif\" alt=\"Print this article\">" +
      "</a>&nbsp;&nbsp;&nbsp;&nbsp;" +
      "<a href=\"emailArticle.asp?issn=0189-6725;year=2024;volume=11;issue=24;spage=101;epage=104;aulast=FOO;type=0\">" +
      "<img border=\"0\" src=\"templates/icon_mail.gif\" alt=\"Email this article\">" +
      "</a>" +
      "</td>" +
      "<td width=\"50%\">" +
      "</td>" +
      "</tr>" +
      "</table>";

  /*
   *  Compare Html and HtmlHashFiltered
   */
  
  public void testBigTOC() throws Exception {

    InputStream actIn = fact.createFilteredInputStream(mau,
        new StringInputStream(bigTOC),
        Constants.DEFAULT_ENCODING);

    assertEquals(bigTOCFiltered, StringUtil.fromInputStream(actIn));

  }
  public void testBigABS() throws Exception {

    InputStream actIn = fact.createFilteredInputStream(mau,
        new StringInputStream(bigAbs),
        Constants.DEFAULT_ENCODING);

    assertEquals(bigAbsFiltered, StringUtil.fromInputStream(actIn));

  }
  public void testAbstract() throws Exception {

    InputStream actIn = fact.createFilteredInputStream(mau,
        new StringInputStream(articleHtml),
        Constants.DEFAULT_ENCODING);

    assertEquals(articleHtmlKept, StringUtil.fromInputStream(actIn));

  }


  public void testCitationCounts() throws Exception {
    InputStream actIn = fact.createFilteredInputStream(mau,
        new StringInputStream(citationCounts),
        Constants.DEFAULT_ENCODING);
    assertEquals(onlyKept, StringUtil.fromInputStream(actIn));
  }

  public void testTOC() throws Exception {
    InputStream actIn = fact.createFilteredInputStream(mau,
        new StringInputStream(tocHtml),
        Constants.DEFAULT_ENCODING);
    assertEquals(tocHtmlKept, StringUtil.fromInputStream(actIn));
  }


}
