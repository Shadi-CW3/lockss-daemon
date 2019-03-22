/*
 * $Id$
 */

/*

Copyright (c) 2000-2017 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.plugin.spandidos;

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.lockss.daemon.*;
import org.lockss.extractor.ArticleMetadataExtractor;
import org.lockss.extractor.ArticleMetadataExtractorFactory;
import org.lockss.extractor.BaseArticleMetadataExtractor;
import org.lockss.extractor.MetadataTarget;
import org.lockss.plugin.*;
import org.lockss.util.Logger;

public class SpandidosArticleIteratorFactory
        implements ArticleIteratorFactory,
        ArticleMetadataExtractorFactory {

  protected static Logger log =
          Logger.getLogger(SpandidosArticleIteratorFactory.class);

  /*
  Article variant format
  https://www.spandidos-publications.com/etm/6/6/1365
  and
    https://www.spandidos-publications.com/etm/6/6/1365?text=abstract
  or
    https://www.spandidos-publications.com/etm/6/6/1365/abstract
  https://www.spandidos-publications.com/ol/15/1/1350?text=fulltext
  https://www.spandidos-publications.com/etm/6/6/1365/download
  */

  // Limit to just journal volume items
  protected static final String ROOT_TEMPLATE = "\"%s%s/%s\", base_url, journal_id, volume_name";
  // Match on only those patters that could be an article
  protected static final String PATTERN_TEMPLATE = "\"https://[^/]+/[^/]+/[^/]+/[^/]+/[^/?]+(/download|/abstract|\\?text=fulltext|\\?text=abstract)?$\"";

  public static final Pattern ABSTRACT_PATTERN = Pattern.compile("/(\\d[^/?]+)/abstract$", Pattern.CASE_INSENSITIVE);
  public static final Pattern ABSTRACT_PATTERN2 = Pattern.compile("/(\\d[^/?]+)\\?text=abstract$", Pattern.CASE_INSENSITIVE);
  public static final Pattern PDF_PATTERN = Pattern.compile("/(\\d[^/?]+)/download$", Pattern.CASE_INSENSITIVE);
  public static final Pattern FULLTEXT_PATTERN2 = Pattern.compile("/(\\d[^/?]+)\\?text=fulltext$", Pattern.CASE_INSENSITIVE);
  public static final Pattern FULLTEXT_PATTERN = Pattern.compile("/(\\d[^/?]+)$", Pattern.CASE_INSENSITIVE);

  public static final String ABSTRACT_REPLACEMENT = "/$1/abstract";
  public static final String ABSTRACT_REPLACEMENT2 = "/$1?text=abstract";
  public static final String PDF_REPLACEMENT = "/$1/download";
  public static final String FULLTEXT_REPLACEMENT2 =  "/$1?text=fulltext";
  public static final String FULLTEXT_REPLACEMENT =  "/$1";

  private static final String FULL_TEXT_ALTERNATIVE = "ROLE_FULL_TEXT_ALTERNATIVE";


  @Override
  public Iterator<ArticleFiles> createArticleIterator(ArchivalUnit au, MetadataTarget target) throws PluginException {
    SubTreeArticleIteratorBuilder builder = new SubTreeArticleIteratorBuilder(au);

    builder.setSpec(target,
            ROOT_TEMPLATE, PATTERN_TEMPLATE, Pattern.CASE_INSENSITIVE);

    // set up Fulltext to be an aspect that will trigger an ArticleFiles
    builder.addAspect(
            FULLTEXT_PATTERN,
            FULLTEXT_REPLACEMENT,
            ArticleFiles.ROLE_FULL_TEXT_HTML,
            ArticleFiles.ROLE_ARTICLE_METADATA);

    // set up PDF to be an aspect that will trigger an ArticleFiles
    builder.addAspect(
            PDF_PATTERN,
            PDF_REPLACEMENT,
            ArticleFiles.ROLE_FULL_TEXT_PDF);

    // set up Abstract to be an aspect that will trigger an ArticleFiles
    builder.addAspect(
            Arrays.asList(ABSTRACT_PATTERN, ABSTRACT_PATTERN2),
            Arrays.asList(ABSTRACT_REPLACEMENT, ABSTRACT_REPLACEMENT2),
            ArticleFiles.ROLE_ABSTRACT);


    builder.addAspect(
            FULLTEXT_REPLACEMENT2,
            FULL_TEXT_ALTERNATIVE);

    // add metadata role from abstract, html
    builder.setRoleFromOtherRoles(
            ArticleFiles.ROLE_ARTICLE_METADATA, ArticleFiles.ROLE_FULL_TEXT_HTML, FULL_TEXT_ALTERNATIVE, ArticleFiles.ROLE_ABSTRACT);

    // The order in which we want to define full_text_cu.
    // First one that exists will get the job
    builder.setFullTextFromRoles(
            ArticleFiles.ROLE_FULL_TEXT_HTML,
            FULL_TEXT_ALTERNATIVE,
            ArticleFiles.ROLE_FULL_TEXT_PDF);


    return builder.getSubTreeArticleIterator();
  }

  protected SubTreeArticleIteratorBuilder localBuilderCreator(ArchivalUnit au) {
    return new SubTreeArticleIteratorBuilder(au);
  }
  @Override
  public ArticleMetadataExtractor createArticleMetadataExtractor(MetadataTarget target)
          throws PluginException {
    return new BaseArticleMetadataExtractor(ArticleFiles.ROLE_ARTICLE_METADATA);
  }
}
