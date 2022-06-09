package org.lockss.plugin.ojs3;

import org.lockss.test.LockssTestCase;
import org.lockss.test.MockArchivalUnit;
import org.lockss.test.StringInputStream;
import org.lockss.util.Constants;
import org.lockss.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

public class TestOjs3HtmlHashFilterFactory  extends LockssTestCase {
  private Ojs3HtmlHashFilterFactory fact;
  private MockArchivalUnit mau;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    fact = new Ojs3HtmlHashFilterFactory();
  }
  private static final String vancouver1 =
    "<div class=\"csl-entry\"><div class=\"csl-right-inline\">Dal PontGE. Lawyer Professionalism in the 21st Century. VULJ [Internet]. 2018Dec.31 [cited 2022May19];8(1):7\u001318. Available from: https://vulj.vu.edu.au/index.php/vulj/article/view/1132</div></div>";

  private static final String vancouver1Filt =
    "<div class=\"csl-entry\"><div class=\"csl-right-inline\">Dal PontGE. Lawyer Professionalism in the 21st Century. VULJ [Internet]. 2018Dec.31 ;8(1):7 18. Available from: https://vulj.vu.edu.au/index.php/vulj/article/view/1132</div></div>";

  private static final String vancouver2 =
      "<div class=\"csl-entry\"><div class=\"csl-left-margin\">1. </div><div class=\"csl-right-inline\">Hebbar PA, Pashilkar AA, Biswas P. Using eye tracking system for aircraft design \u0013 a flight simulator study. Aviation [Internet]. 2022Mar.22 [cited 2022 May 20];26(1):11\u001321. Available from: https://journals.vilniustech.lt/index.php/Aviation/article/view/16398</div></div>";

  private static final String vancouver2Filt =
      "<div class=\"csl-entry\"><div class=\"csl-left-margin\">1. </div><div class=\"csl-right-inline\">Hebbar PA, Pashilkar AA, Biswas P. Using eye tracking system for aircraft design a flight simulator study. Aviation [Internet]. 2022Mar.22 ;26(1):11 21. Available from: https://journals.vilniustech.lt/index.php/Aviation/article/view/16398</div></div>";

  private static final String vancouver3 =
      "<div class=\"csl-bib-body\">\n" +
          "  <div class=\"csl-entry\"><div class=\"csl-left-margin\">1.</div><div class=\"csl-right-inline\">Gross A. The Continuity of Scientific Discovery and Its Communication: The Example of Michael Faraday. Disc Collab [Internet]. 2009 Feb. 27 [cited 2022 Jun. 9];4:3. Available from: https://journals.uic.edu/ojs/index.php/jbdc/article/view/2444</div></div>\n" +
          "</div>";

  private static final String vancouver3Filt =
      "<div class=\"csl-entry\"><div class=\"csl-left-margin\">1.</div><div class=\"csl-right-inline\">Gross A. The Continuity of Scientific Discovery and Its Communication: The Example of Michael Faraday. Disc Collab [Internet]. 2009 Feb. 27 ;4:3. Available from: https://journals.uic.edu/ojs/index.php/jbdc/article/view/2444</div></div>";

  private static final String tarubian =
      "<div class=\"csl-entry\">Zeller, Bruno, and Richard Lightfoot. \u001CGood Faith: An ICSID Convention Requirement?\u001D. <i>Victoria University Law and Justice Journal</i> 8, no. 1 (December 31, 2018): 19\u001332. Accessed May 19, 2022. https://vulj.vu.edu.au/index.php/vulj/article/view/1139.</div>";

  private static final String tarubianFilt =
      "<div class=\"csl-entry\">Zeller, Bruno, and Richard Lightfoot. Good Faith: An ICSID Convention Requirement? . <i>Victoria University Law and Justice Journal</i> 8, no. 1 (December 31, 2018): 19 32. . https://vulj.vu.edu.au/index.php/vulj/article/view/1139.</div>";

  private static final String harvard =
      "<div class=\"csl-entry\">Mendoza, R. \u001CRuby\u001D . (2022) \u001CBook Review\u0014The Borders of AIDS: Race, Quarantine &amp; Resistance by Karma R. Chavez\u001D, <i>Literacy in Composition Studies</i>, 9(2), pp. 67-72. Available at: https://licsjournal.org/index.php/LiCS/article/view/2193 (Accessed: 21May2022).</div>";

  private static final String harvardFilt =
      "<div class=\"csl-entry\">Mendoza, R. Ruby . (2022) Book Review The Borders of AIDS: Race, Quarantine &amp; Resistance by Karma R. Chavez , <i>Literacy in Composition Studies</i>, 9(2), pp. 67-72. Available at: https://licsjournal.org/index.php/LiCS/article/view/2193 .</div>";

  private static final String abdnt =
      "<div class=\"csl-entry\">DRERUP, C.; EVESLAGE, M.; SUNDERKTTER, C.; EHRCHEN, J. Diagnostic Value of Laboratory Parameters for Distinguishing Between Herpes Zoster and Bacterial Superficial Skin and Soft Tissue Infections. <b>Acta Dermato-Venereologica</b>, <i>[S. l.]</i>, v. 100, n. 1, p. 1\u00135, 2020. DOI: 10.2340/00015555-3357. Disponvel em: https://medicaljournalssweden.se/actadv/article/view/1631. Acesso em: 19 may. 2022.</div>";

  private static final String abdntFilt =
      "<div class=\"csl-entry\">DRERUP, C.; EVESLAGE, M.; SUNDERKTTER, C.; EHRCHEN, J. Diagnostic Value of Laboratory Parameters for Distinguishing Between Herpes Zoster and Bacterial Superficial Skin and Soft Tissue Infections. <b>Acta Dermato-Venereologica</b>, <i>[S. l.]</i>, v. 100, n. 1, p. 1 5, 2020. DOI: 10.2340/00015555-3357. Disponvel em: https://medicaljournalssweden.se/actadv/article/view/1631. .</div>";

  private static final String jatsArticle =
    "<div class=\"jatsParser__center-article-block\">\n" +
      "<div class=\"jatsParser__article-fulltext\" id=\"jatsParserFullText\">\n" +
        "<h2 class=\"article-section-title jatsParser__abstract\">Abstract</h2>\n" +
        "The genus <em>Mahanarva</em> Distant, 1909 (Hemiptera: Cercopoidea: Cercopidae) currently includes two subgenera: <em>Mahanarva</em> Distant, 1909 with 38 species and six subspecies, and <em>Ipiranga</em> Fennah, 1968 with nine species. The <em>Manaharva</em> species are all from the Americas, and a few species are important pests in pasture grasses and sugarcane. There are no reports of any <em>Manaharva</em> species from North America, including Mexico and areas to the north. Here, a new species is described from Mexico and a key to the species of <em>Mahanarva</em><strong> </strong>from Central America and Mexico is proposed." +
      "</div>" +
    "</div>";

  private static final String jatsArticleFilt =
    "<div class=\"jatsParser__article-fulltext\" id=\"jatsParserFullText\"> " +
      "<h2 class=\"article-section-title jatsParser__abstract\">Abstract</h2> " +
      "The genus <em>Mahanarva</em> Distant, 1909 (Hemiptera: Cercopoidea: Cercopidae) currently includes two subgenera: <em>Mahanarva</em> Distant, 1909 with 38 species and six subspecies, and <em>Ipiranga</em> Fennah, 1968 with nine species. The <em>Manaharva</em> species are all from the Americas, and a few species are important pests in pasture grasses and sugarcane. There are no reports of any <em>Manaharva</em> species from North America, including Mexico and areas to the north. Here, a new species is described from Mexico and a key to the species of <em>Mahanarva</em><strong> </strong>from Central America and Mexico is proposed." +
    "</div>";

  public void testAbdntFiltering() throws Exception {
    assertEquals(getStringfromFilteredInputStream(abdnt), abdntFilt);
  }
  public void testVancouverFiltering() throws Exception {
    assertEquals(getStringfromFilteredInputStream(vancouver1), vancouver1Filt);
  }
  public void testVancouverBFiltering() throws Exception {
    assertEquals(getStringfromFilteredInputStream(vancouver2), vancouver2Filt);
  }
  public void testVancouver3iltering() throws Exception {
    assertEquals(getStringfromFilteredInputStream(vancouver3), vancouver3Filt);
  }
  public void testHarvardFiltering() throws Exception {
    assertEquals(getStringfromFilteredInputStream(harvard), harvardFilt);
  }
  public void testTarubianFiltering() throws Exception {
    assertEquals(getStringfromFilteredInputStream(tarubian), tarubianFilt);
  }
  public void testJatsArtFiltering() throws Exception {
    assertEquals(getStringfromFilteredInputStream(jatsArticle), jatsArticleFilt);
  }

  public String getStringfromFilteredInputStream(String in) throws IOException {
    InputStream actIn = fact.createFilteredInputStream(mau,
        new StringInputStream(in),
        Constants.DEFAULT_ENCODING);
    return StringUtil.fromInputStream(actIn);
  }
}
