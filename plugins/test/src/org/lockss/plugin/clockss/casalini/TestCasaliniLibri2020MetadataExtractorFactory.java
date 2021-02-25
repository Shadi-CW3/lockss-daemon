package org.lockss.plugin.clockss.casalini;

import org.lockss.util.UrlUtil;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.marc.Record;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Leader;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.lockss.daemon.PluginException;
import org.lockss.extractor.*;
import org.lockss.plugin.CachedUrl;
import org.lockss.plugin.clockss.SourceXmlMetadataExtractorFactory;
import org.lockss.plugin.clockss.SourceXmlMetadataExtractorTest;
import org.lockss.plugin.clockss.SourceXmlSchemaHelper;
import org.lockss.test.MockArchivalUnit;
import org.lockss.test.MockCachedUrl;
import org.lockss.util.CIProperties;
import org.lockss.util.Constants;
import org.lockss.util.Logger;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCasaliniLibri2020MetadataExtractorFactory extends SourceXmlMetadataExtractorTest {

    private static final Logger log = Logger.getLogger(TestCasaliniLibriMarcXmlMetadataExtractorFactory.class);

    private static String BaseUrl = "http://source.host.org/sourcefiles/casalini/";
    private static String Directory = "2019";
    private static String pdfUrl1 = BaseUrl + Directory + "/2000_4_2194804.pdf";
    private static String pdfUrl2 = BaseUrl + Directory + "/2002_4_2194812.pdf";

    private static final Map<String,String> PublisherNameShortcutMap = new HashMap<String,String>();

    private static final String COLLECTION_NAME = "Monographs";

    static {
        PublisherNameShortcutMap.put("Edizioni dell'Ateneo", "ATENEO");
        PublisherNameShortcutMap.put("Cadmo", "CADMO");
        PublisherNameShortcutMap.put("Casalini libri", "CASA");
        PublisherNameShortcutMap.put("CLUEB", "CLUEB");
        PublisherNameShortcutMap.put("Gruppo editoriale internazionale", "GEI");
        PublisherNameShortcutMap.put("Giardini", "GIARDI");
        PublisherNameShortcutMap.put("Istituti editoriali e poligrafici internazionali", "IEPI");
    };

    private static String getXmlFileContent(String fname) {
        String xmlContent = "";
        try {
            String currentDirectory = System.getProperty("user.dir");
            String pathname = currentDirectory +
                    "/plugins/test/src/org/lockss/plugin/clockss/casalini/" + fname;
            xmlContent = FileUtils.readFileToString(new File(pathname), Constants.DEFAULT_ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlContent;
    }

    public void testGeneratedXmlFromMrcFormat() throws Exception {

        String fname = "Sample.mrc";

        String samplePath = "./plugins/test/src/org/lockss/plugin/clockss/casalini/" + fname;

        /*
        InputStream input = new FileInputStream(samplePath);
        OutputStream out = new FileOutputStream(new File("./plugins/test/src/org/lockss/plugin/clockss/casalini/generated.xml"));


        MarcReader reader = new MarcStreamReader(input);
        MarcWriter writer = new MarcXmlWriter(out, true);

        AnselToUnicode converter = new AnselToUnicode();
        writer.setConverter(converter);

        while (reader.hasNext()) {
            Record record = reader.next();
            writer.write(record);
        }
        writer.close();
         */
    }

    public void testReadingMrcXmlByMrcBinary() throws Exception {

        //String fname = "Marc21SampleArticle.xml";
        //String fname = "Sample.mrc";
        String fname = "Marc212016Sample.xml";

        String samplePath = "./plugins/test/src/org/lockss/plugin/clockss/casalini/" + fname;

        InputStream input = new FileInputStream(samplePath);

        MarcReader reader = null;

        if (fname.contains(".xml")) {
            reader = new MarcXmlReader(input);
        }

        if (fname.contains(".mrc")) {
            reader = new MarcStreamReader(input);
        }

        //log.info("------------fname: " + fname);

        while (reader.hasNext()) {
            Record record = reader.next();

            Leader leader = record.getLeader();

            char publication_type1 = leader.getImplDefined1()[0];
            char publication_type2 = leader.getImplDefined1()[1];

            log.info(String.format("--------publication_type1: %c", publication_type1));
            log.info(String.format("--------publication_type2: %c", publication_type2));

            List<DataField> fields = record.getDataFields();
            if (fields != null) {
                for (DataField field : fields) {
                    String tag = field.getTag();
                    List<Subfield> subfields = field.getSubfields();
                    if (subfields != null) {
                        for (Subfield subfield : subfields) {
                            char subtag = subfield.getCode();
                            String subtag_data = subfield.getData();
                            log.info(String.format("--------%s_%c: %s", tag, subtag, subtag_data));
                        }
                    }
                }
            }

            String MARC_isbn = getMARCData(record, "020", 'a');
            String MARC_issn = getMARCData(record, "022", 'a');
            String MARC_title = getMARCData(record, "245", 'a');
            String MARC_pub_date =  getMARCData(record, "260", 'c');
            String MARC_publisher = getMARCData(record, "260", 'b');
            String MARC_publisher_alt = getMARCData(record, "264", 'b');
            String MARC_total_page = getMARCData(record, "300", 'a');
            String MARC_author =   getMARCData(record, "100", 'a');
            String MARC_author_alt =   getMARCData(record, "700", 'a');

            String MARC_bookid =   getMARCData(record, "097", 'a');
            String MARC_chapterid =   getMARCData(record, "097", 'c');

            String publisherCleanName = MARC_publisher.replace(",", "");
            String publisherShortCut = PublisherNameShortcutMap.get(publisherCleanName);

            if (publisherShortCut != null) {
                log.info(String.format("-------MARC_publisher: %s | publisherCleanName: %s | publisherShortCut: %s",
                        MARC_publisher, publisherCleanName, publisherShortCut));

            } else {
                log.info(String.format("=======MARC_publisher: %s | publisherCleanName: %s",
                        MARC_publisher, publisherCleanName));

            }

            log.info(String.format("-------MARC_publisher: %s | publisherCleanName: %s | publisherShortCut: %s",
                    MARC_publisher, publisherCleanName, publisherShortCut));

            String MARC_pdf =  String.format("/%s/%s/%s/%s.pdf", COLLECTION_NAME, publisherShortCut, MARC_bookid, MARC_bookid);

            log.info("-------MARC_pdf: " + MARC_pdf );

            if (MARC_bookid.equals(MARC_chapterid)) {
                log.info(String.format("----------------Emit chapter: MARC_bookid %s | MARC_chapterid: %s ",
                        MARC_bookid, MARC_chapterid));

            } else {
                log.info(String.format("Do not emit chapter: MARC_bookid %s | MARC_chapterid: %s ",
                        MARC_bookid, MARC_chapterid));
            }
        }
    }

    private String getMARCData(Record record, String dataFieldCode, char subFieldCode) {

        try {
            DataField field = (DataField) record.getVariableField(dataFieldCode);

            // It is not guaranteed each record has the same information
            if (field != null) {

                String tag = field.getTag();
                char ind1 = field.getIndicator1();
                char ind2 = field.getIndicator2();

                log.debug3("Mrc Record Tag: " + tag + " Indicator 1: " + ind1 + " Indicator 2: " + ind2);

                List subfields = field.getSubfields();
                Iterator i = subfields.iterator();

                while (i.hasNext()) {
                    Subfield subfield = (Subfield) i.next();
                    char code = subfield.getCode();
                    String data = subfield.getData();

                    if (code == subFieldCode) {
                        log.debug3("Mrc Record Found Tag: " + tag + " Subfield code: " + code + " Data element: " + data);

                        // clean up data before return
                        if (dataFieldCode.equals("700")) {
                            return data.replace("edited by", "");
                        } else if (dataFieldCode.equals("300")) {
                            return data.replace("p.", "").replace(":", "");
                        } else if (dataFieldCode.equals("245") && subFieldCode == 'a') {
                            return data.replace("/", "").replace(":", "");
                        } else {
                            return data;
                        }
                    }
                }
            } else {
                log.debug3("Mrc Record getVariableField: " + dataFieldCode + " return null");
            }
        } catch (NullPointerException e) {
            log.debug3("Mrc Record DataFieldCode: " + dataFieldCode + " SubFieldCode: " + subFieldCode + " has error");
        }
        return null;
    }
}


