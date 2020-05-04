package org.lockss.plugin.clockss.oxforduniversitypress;

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


public class OxfordUniversityPressXmlSourceArticleIteratorFactory implements ArticleIteratorFactory, ArticleMetadataExtractorFactory {

    //https://clockss-test.lockss.org/sourcefiles/oup-released/2020/22apr.test/neurosurgery_10.1093_neuros_nyy531.zip!/10.1093_neuros_nyy531/NEUROSURGERY_86_3_317.xml
    //https://clockss-test.lockss.org/sourcefiles/oup-released/2020/22apr.test/neurosurgery_10.1093_neuros_nyy531.zip!/pdf/NEUROSURGERY_86_3_317.pdf
    protected static Logger log = Logger.getLogger(OxfordUniversityPressXmlSourceArticleIteratorFactory.class);

    protected static final String ALL_ZIP_XML_PATTERN_TEMPLATE =
            "\"%s.*\\.zip!/(.*)/(.*)\\.xml$\", base_url, directory";

    // Be sure to exclude all nested archives in case supplemental data is provided this way
    protected static final Pattern SUB_NESTED_ARCHIVE_PATTERN =
            Pattern.compile(".*/.*\\.zip!/.+\\.(zip|tar|gz|tgz|tar\\.gz)$",
                    Pattern.CASE_INSENSITIVE);

    protected Pattern getExcludeSubTreePattern() {
        return SUB_NESTED_ARCHIVE_PATTERN;
    }

    protected String getIncludePatternTemplate() {
        return ALL_ZIP_XML_PATTERN_TEMPLATE;
    }

    public static final Pattern XML_PATTERN = Pattern.compile("/(.*)/(.*)\\.xml$", Pattern.CASE_INSENSITIVE);
    public static final Pattern PDF_PATTERN = Pattern.compile("/(.*)/(.*)\\.pdf$", Pattern.CASE_INSENSITIVE);
    public static final String XML_REPLACEMENT = "/$1/$2.xml";
    private static final String PDF_REPLACEMENT = "/pdf/$2.pdf";

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