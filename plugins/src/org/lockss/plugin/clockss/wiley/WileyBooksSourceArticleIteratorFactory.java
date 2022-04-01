/*
 * $Id$
 */

/*

Copyright (c) 2000-2014 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.plugin.clockss.wiley;

import org.lockss.daemon.PluginException;
import org.lockss.extractor.ArticleMetadataExtractor;
import org.lockss.extractor.ArticleMetadataExtractorFactory;
import org.lockss.extractor.BaseArticleMetadataExtractor;
import org.lockss.extractor.MetadataTarget;
import org.lockss.plugin.ArchivalUnit;
import org.lockss.plugin.ArticleFiles;
import org.lockss.plugin.ArticleIteratorFactory;
import org.lockss.plugin.SubTreeArticleIteratorBuilder;
import org.lockss.util.Logger;

import java.util.Iterator;
import java.util.regex.Pattern;

public class WileyBooksSourceArticleIteratorFactory implements ArticleIteratorFactory, ArticleMetadataExtractorFactory {

  protected static Logger log = Logger.getLogger(WileyBooksSourceArticleIteratorFactory.class);

  /*
  Each book is a zip file. But there is no PDF file for the whole book, it is chapter based + additional stuff
  We will use  "fmatter.xml" to extract metadata from, since each book only report as one item in report.

  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/ch7/ch7.pdf
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/ch7/ch7.xml
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/ch8/ch8.pdf
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/ch8/ch8.xml
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/ch9/ch9.pdf
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/ch9/ch9.xml
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/cover.gif
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/fmatter/fmatter.pdf
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/fmatter/fmatter.xml
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/gloss/gloss.pdf
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/gloss/gloss.xml
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/index/index.pdf
  https://clockss-test.lockss.org/sourcefiles/wileybooks-released/2022_01/9780470007259.zip!/index/index.xml
   */

  protected static final String ALL_ZIP_XML_PATTERN_TEMPLATE =
          "\"%s[^/]+/.*\\.zip!/(.*)\\.(xml|pdf)$\", base_url";

  // Be sure to exclude all nested archives in case supplemental data is provided this way
  protected static final Pattern SUB_NESTED_ARCHIVE_PATTERN =
          Pattern.compile(".*/[^/]+\\.zip!/.+\\.(zip|tar|gz|tgz|tar\\.gz)$",
                  Pattern.CASE_INSENSITIVE);

  protected Pattern getExcludeSubTreePattern() {
    return SUB_NESTED_ARCHIVE_PATTERN;
  }

  protected String getIncludePatternTemplate() {
    return ALL_ZIP_XML_PATTERN_TEMPLATE;
  }

  public static final Pattern XML_PATTERN = Pattern.compile("/fmatter/(.*)\\.xml$", Pattern.CASE_INSENSITIVE);
  public static final Pattern PDF_PATTERN = Pattern.compile("/fmatter/(.*)\\.pdf$", Pattern.CASE_INSENSITIVE);
  public static final String XML_REPLACEMENT = "/fmatter/$1.xml";
  private static final String PDF_REPLACEMENT = "/fmatter/$1.pdf";

  @Override
  public Iterator<ArticleFiles> createArticleIterator(ArchivalUnit au,
                                                      MetadataTarget target)
          throws PluginException {
    SubTreeArticleIteratorBuilder builder = new SubTreeArticleIteratorBuilder(au);

    // no need to limit to ROOT_TEMPLATE
    builder.setSpec(builder.newSpec()
            .setTarget(target)
            .setPatternTemplate(getIncludePatternTemplate(), Pattern.CASE_INSENSITIVE)
            .setExcludeSubTreePattern(getExcludeSubTreePattern())
            .setVisitArchiveMembers(true)
            .setVisitArchiveMembers(getIsArchive()));

    builder.addAspect(PDF_PATTERN,
            PDF_REPLACEMENT,
            ArticleFiles.ROLE_FULL_TEXT_PDF);

    builder.addAspect(XML_PATTERN,
            XML_REPLACEMENT,
            ArticleFiles.ROLE_ARTICLE_METADATA);

    builder.setFullTextFromRoles(ArticleFiles.ROLE_FULL_TEXT_PDF,
            ArticleFiles.ROLE_ARTICLE_METADATA);

    return builder.getSubTreeArticleIterator();
  }

  // NOTE - for a child to create their own version of this
  // indicates if the iterator should descend in to archives (for tar/zip deliveries)
  protected boolean getIsArchive() {
    return true;
  }

  @Override
  public ArticleMetadataExtractor createArticleMetadataExtractor(MetadataTarget target)
          throws PluginException {
    return new BaseArticleMetadataExtractor(ArticleFiles.ROLE_ARTICLE_METADATA);
  }

}
