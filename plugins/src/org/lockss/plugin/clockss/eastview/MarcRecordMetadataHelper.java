/*
 * $Id$
 */

/*

 Copyright (c) 2000-2016 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.plugin.clockss.eastview;

import org.apache.commons.io.FilenameUtils;
import org.lockss.config.TdbAu;
import org.lockss.daemon.PluginException;
import org.lockss.extractor.ArticleMetadata;
import org.lockss.extractor.FileMetadataExtractor;
import org.lockss.extractor.MetadataField;
import org.lockss.extractor.MetadataTarget;
import org.lockss.plugin.CachedUrl;
import org.lockss.plugin.clockss.MetadataStringHelperUtilities;
import org.lockss.util.Logger;
import org.lockss.util.UrlUtil;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Marc4J library https://github.com/marc4j/marc4j
 */
public class MarcRecordMetadataHelper implements FileMetadataExtractor {

  private static final Logger log = Logger.getLogger(MarcRecordMetadataHelper.class);

  private static Pattern DOI_PAT = Pattern.compile("10[.][0-9a-z]{4,6}/.*");


  @Override
  public void extract(MetadataTarget target, CachedUrl cu, Emitter emitter) throws IOException, PluginException {

    try {

      InputStream input = cu.getUnfilteredInputStream();

      MarcReader reader = null;

      if (cu.getUrl().contains(".xml")) {
        reader = new MarcXmlReader(input, "UTF-8");
      }

      if (cu.getUrl().contains(".mrc")) {
        reader = new MarcStreamReader(input, "UTF-8");
      }

      int recordCount = 0;
      List<String> bookIDs = new ArrayList<String>();

      while (reader.hasNext()) {

        ArticleMetadata am = new ArticleMetadata();

        Record record = reader.next();
        recordCount++;

        // Get all the raw metadata and put it to raw
        List<DataField> fields = record.getDataFields();
        if (fields != null) {
          for (DataField field : fields) {
            String tag = field.getTag();
            List<Subfield> subfields = field.getSubfields();
            if (subfields != null) {
              for (Subfield subfield : subfields) {
                char subtag = subfield.getCode();
                log.debug(String.format("Raw data: %s_%c: %s", tag, subtag,subfield.getData()));
                am.putRaw(String.format("%s_%c", tag, subtag), subfield.getData());
              }
            } else {
              log.debug("raw subfield is null");
            }
          }
        } else {
          log.debug("raw field is null");
        }

        String MARC_isbn = getMARCData(record, "020", 'a');
        // This is only used in 2016 mrc record
        String MARC_isbn_alt = getMARCData(record, "773", 'z');
        String MARC_issn = getMARCData(record, "022", 'a');
        // MARC_Title will be different for journal vs books
        String MARC_title = getMARCData(record, "245", 'a');
        String MARC_pub_date = getMARCData(record, "260", 'c');
        String MARC_pub_date_alt = getMARCData(record, "264", 'c');
        String MARC_publisher = getMARCData(record, "260", 'b');
        String MARC_author = getMARCData(record, "100", 'a');
        String MARC_author_alt = getMARCData(record, "700", 'a');
        String MARC_doi =  getMARCData(record, "024", 'a');
        String MARC_doi_alt =  getMARCData(record, "856", 'u');
        String MARC_pdf = getMARCControlFieldData(record, "001");
        // Add it to raw metadata
        am.putRaw("mrc_controlfield_001", MARC_pdf);

        //Set DOI
        if (MARC_doi != null && isDoi(MARC_doi)) {
          log.debug("MARC_doi:" + MARC_doi );
          am.put(MetadataField.FIELD_DOI, MARC_doi);
        } else if (MARC_doi_alt != null )  {
          if (isDoi(MARC_doi_alt)) {
            log.debug("MARC_doi_alt:" + MARC_doi_alt);
            am.put(MetadataField.FIELD_DOI, MARC_doi_alt);
          }
        }

        // Set ISBN
        if (MARC_isbn != null) {
          log.debug("MARC_isbn:" + MARC_isbn);
          am.put(MetadataField.FIELD_ISBN, MARC_isbn);
        } else if (MARC_isbn_alt != null) {
          log.debug("MARC_isbn_alt:" + MARC_isbn_alt);
          am.put(MetadataField.FIELD_ISBN, MARC_isbn_alt);
        }

        // Set publiation date
        if (MARC_pub_date != null) {
          log.debug("MARC_pub_date:" + MARC_pub_date);
          am.put(MetadataField.FIELD_DATE, MetadataStringHelperUtilities.cleanupPubDate(MARC_pub_date));
        } else if (MARC_pub_date_alt != null) {
          log.debug("MARC_pub_date_alt:" + MARC_pub_date_alt);
          am.put(MetadataField.FIELD_DATE, MetadataStringHelperUtilities.cleanupPubDate(MARC_pub_date_alt));
        }

        // Set author
        if (MARC_author_alt != null) {
          log.debug("MARC_author_alt:" + MARC_author_alt);
          am.put(MetadataField.FIELD_AUTHOR, MARC_author_alt.replace(".", ""));
        } else if (MARC_author != null) {
            log.debug("MARC_author:" + MARC_author);
            am.put(MetadataField.FIELD_AUTHOR, MARC_author.replace(".", ""));
        }

        // Set publisher name
        String publisherName = "East View Information Services";

        TdbAu tdbau = cu.getArchivalUnit().getTdbAu();
        if (tdbau != null) {
          publisherName = tdbau.getPublisherName();
        }

        am.put(MetadataField.FIELD_PUBLISHER, publisherName);

        // Setup PDF 
        String zippedFolderName = EastviewMarcXmlSchemaHelper.getZippedFolderName();

        String fileNum = MARC_pdf;
        String cuBase = FilenameUtils.getFullPath(cu.getUrl());

        String pdfFilePath = cuBase + zippedFolderName + ".zip!/" + fileNum + ".pdf";
        log.debug("pdfFilePath" + pdfFilePath );
        am.put(MetadataField.FIELD_ACCESS_URL, pdfFilePath);

        /*
        Leader byte 07 “a” = Book (monographic component part)
        Leader byte 07 “m” = Book
        Leader byte 07 “s” = Journal
        Leader byte 07 “b” = Journal (serial component part)
        */

        Leader leader = record.getLeader();

        char publication_type = '0';

        publication_type = leader.getImplDefined1()[0];

        if (publication_type != '0') {
          // Setup FIELD_PUBLICATION_TYPE & FIELD_ARTICLE_TYPE
          if (publication_type == 'm' || publication_type == 'a') {
            am.put(MetadataField.FIELD_ARTICLE_TYPE, MetadataField.ARTICLE_TYPE_BOOKVOLUME);
            am.put(MetadataField.FIELD_PUBLICATION_TYPE, MetadataField.PUBLICATION_TYPE_BOOK);

            // Set publication title
            if (MARC_title != null) {
              String cleanedArticleTitle = MARC_title.replace(":", "").
                      replace("/", "").
                      replace("=", "").
                      replace("\"", "").
                      replace("...", "");
              log.debug(String.format("original artitle title = %s, cleaned title = %s",MARC_title, cleanedArticleTitle));
              am.put(MetadataField.FIELD_PUBLICATION_TITLE, MARC_title);
            }
          }

          if (publication_type == 's' || publication_type == 'b') {
            am.put(MetadataField.FIELD_ARTICLE_TYPE, MetadataField.ARTICLE_TYPE_JOURNALARTICLE);
            am.put(MetadataField.FIELD_PUBLICATION_TYPE, MetadataField.PUBLICATION_TYPE_JOURNAL);

            // Set publication title
            if (MARC_title != null) {
              log.debug("MARC_title:" + MARC_title);
              am.put(MetadataField.FIELD_ARTICLE_TITLE, MARC_title);
            }
            
            // Set ISSN
            if (MARC_issn != null) {
              log.debug("MARC_issn:" + MARC_issn);
              am.put(MetadataField.FIELD_ISSN, MARC_issn);
            }
          }
        }

        emitter.emitMetadata(cu, am);
      }

    } catch (NullPointerException exception) {
      exception.printStackTrace();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Get MARC21 data value by dataFieldCode and subFieldCode
   * @param record
   * @param dataFieldCode
   * @param subFieldCode
   * @return String value of MARC21 data field
   */
  public static String getMARCData(Record record, String dataFieldCode, char subFieldCode) {

    try {
      DataField field = (DataField) record.getVariableField(dataFieldCode);

      // It is not guaranteed each record has the same information
      if (field != null) {

        String tag = field.getTag();
        char ind1 = field.getIndicator1();
        char ind2 = field.getIndicator2();

        List subfields = field.getSubfields();
        Iterator i = subfields.iterator();

        while (i.hasNext()) {
          Subfield subfield = (Subfield) i.next();
          char code = subfield.getCode();
          String data = subfield.getData();

          if (code == subFieldCode) {
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
      }
    } catch (NullPointerException e) {
      log.debug("Mrc Record DataFieldCode: " + dataFieldCode + " SubFieldCode: " + subFieldCode + " has error");
    }
    return null;
  }

  /**
   * Get MARC21 control field data by dataFieldCode
   * @param record
   * @param dataFieldCode
   * @return String value of control field
   */
  private String getMARCControlFieldData(Record record, String dataFieldCode) {

    ControlField field = (ControlField) record.getVariableField(dataFieldCode);

    if (field != null) {
      String data = field.getData();
      log.debug("Mrc Record getMARCControlFieldData: " + data);
      return data;
    } else {
        log.debug("Mrc Record getMARCControlFieldData: " + dataFieldCode + " return null");
        return null;
    }
  }

  private boolean isDoi(String doi) {

    if (doi == null) {
      return false;
    }
    Matcher m = DOI_PAT.matcher(doi);

    if(!m.matches()){
      return false;
    }
    return true;
  }

}