package org.lockss.plugin.clockss.igpublishing;

import org.lockss.config.TdbAu;
import org.lockss.daemon.PluginException;
import org.lockss.daemon.ShouldNotHappenException;
import org.lockss.extractor.ArticleMetadata;
import org.lockss.extractor.FileMetadataExtractor;
import org.lockss.extractor.MetadataField;
import org.lockss.extractor.MetadataTarget;
import org.lockss.plugin.CachedUrl;
import org.lockss.plugin.clockss.SourceXmlMetadataExtractorFactory;
import org.lockss.plugin.clockss.SourceXmlSchemaHelper;
import org.lockss.util.Logger;
import org.w3c.dom.Document;

public class IGPublishingMetadataExtractorFactory extends SourceXmlMetadataExtractorFactory {
    private static final Logger log = Logger.getLogger(IGPublishingMetadataExtractorFactory.class);

    private static SourceXmlSchemaHelper IGPublishingXmlHelper = null;

    @Override
    public FileMetadataExtractor createFileMetadataExtractor(MetadataTarget target,
                                                             String contentType)
            throws PluginException {
        return new IGPublishingMetadataExtractor();
    }

    public class IGPublishingMetadataExtractor extends SourceXmlMetadataExtractor {

        @Override
        protected SourceXmlSchemaHelper setUpSchema(CachedUrl cu) {
            throw new ShouldNotHappenException("This version of the schema setup cannot be used for this plugin");
        }
        
        @Override
        protected SourceXmlSchemaHelper setUpSchema(CachedUrl cu, Document doc) {

            if (IGPublishingXmlHelper == null) {
                IGPublishingXmlHelper = new IGPublishingSchemaHelper();
            }
            return IGPublishingXmlHelper;
        }

        @Override
        protected void postCookProcess(SourceXmlSchemaHelper schemaHelper,
                                       CachedUrl cu, ArticleMetadata thisAM) {

            String publisherName = "IG Publishing";

            TdbAu tdbau = cu.getArchivalUnit().getTdbAu();
            if (tdbau != null) {
                publisherName =  tdbau.getPublisherName();
            }

            thisAM.put(MetadataField.FIELD_PUBLISHER, publisherName);
            thisAM.put(MetadataField.FIELD_PROVIDER, publisherName);
            
        }
    }
}

