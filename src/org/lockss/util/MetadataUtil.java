/*
 * $Id: MetadataUtil.java,v 1.15 2012-02-25 00:25:23 akanshab01 Exp $
 */

/*

Copyright (c) 2000-2010 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.util;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.commons.lang.LocaleUtils;

import org.lockss.config.*;
import org.lockss.app.*;
import org.lockss.daemon.*;
import org.lockss.extractor.*;
import org.lockss.plugin.*;
import static org.lockss.extractor.MetadataField.*;

public class MetadataUtil {

  static Logger log = Logger.getLogger("MetadataUtil");

  public static String getTargetMimeType(MetadataTarget target,
					 ArchivalUnit au) {
    String mimeType = target != null ? target.getFormat() : null;
    if (mimeType == null) {
      mimeType = au.getPlugin().getDefaultArticleMimeType();
    }
    return mimeType;
  }

  private static Pattern ISSN_PAT =
    Pattern.compile("\\d{4}-\\d{3}[\\d{1}|x{1}|X{1}]");
  
  private static Pattern AUTHOR_PAT =
   // pattern to check whether it has atleast one letter.   
    Pattern.compile(".*\\p{L}");

  /**
   * Check that ISSN is valid. If it is, return the ISSN, otherwise return
   * <tt>null</tt>. Check digit is not verified.
   *
   * @param issn the issn string
   * @return the issn if it is valid, <tt>null</tt> otherwise
   */
  public static String validateIssn(String issn) {
    return validateIssn(issn, false);
  }

  /**
   * Check that ISSN is valid. If it is, return the ISSN, otherwise return
   * <tt>null</tt>. Check digit is verified.
   *
   * @param issn the issn string
   * @param strict if true, also verify checksum, otherwise just check form
   * @return the issn if it is valid, <tt>null</tt> otherwise
   */
  public static String validateIssn(String issn, boolean strict) {
    return isIssn(issn, strict) ? issn : null;
  }

  /**
   * Check that ISSN is valid. Method checks that ISSN number is correctly
   * balanced (4 digits on either side of a hyphen). Check digit is not
   * verified.
   * 
   * @param issn the issn string
   * @param strict if true, also verify checksum, otherwise just check form
   * @return true if issn is valid, false otherwise
   */
  public static boolean isIssn(String issn) {
    return isIssn(issn, false);
  }
  
  /**
   * Check that ISSN is valid. Method checks that ISSN number is correctly
   * balanced (4 digits on either side of a hyphen) and that the check
   * digit (rightmost digit) is valid.
   * <p>
   * <strong>Note:</strong> Due to errors at a publishing house that go 
   * undetected, journals have been issued with invalid ISSNs. (e.g. 1234-5678).
   * If the strict flag is true, verify checksum, otherwise just verify form.
   * 
   * @param issn the issn string
   * @param strict if true, also verify checksum, otherwise just check form
   * @return true if issn is valid, false otherwise
   */
  public static boolean isIssn(String issn, boolean strict) {

    if (issn == null) {
      return false;
    }
    Matcher m = ISSN_PAT.matcher(issn);

    if(!m.matches()){
      return false;
    }
    
    // matches form of an ISSN if not doing stricvalidateIssnt checking
    if (!strict) {
      return true;
    }
    
    String issnArr[] = issn.split("-");
    String issnStr = issnArr[0] + "" + issnArr[1];

    char issnChars[] = issnStr.toCharArray();
    int checkSum = 0;
    
    // calculate what the check digit should be
    for (int i = 0; i < issnChars.length - 1; i++) {
      checkSum += Integer.parseInt(String.valueOf(issnChars[i])) * (issnChars.length - i);
    }

    char checkDigitChar = issnChars[issnChars.length - 1];
    int checkDigit;

    // a check digit of X means it's digit 10 in roman numerals
    if (String.valueOf(checkDigitChar).equalsIgnoreCase("x")) {
      checkDigit = 10;
    } else {
      checkDigit = Integer.parseInt(String.valueOf(checkDigitChar));
    }

    int remainder = checkSum % 11;

    if (checkDigit != 0 && remainder == 0) {
      return false;
    } else if (checkDigit == 0 && remainder != 0) {
      return false;
    } else if (checkDigit != 0 && 11 - remainder != checkDigit) {
      return false;
    }


    return true;
  }
  
  /**
   * Return formatted form of ISSN with hyphens. This method
   * does not check for the vaildity of the input ISSN.
   * 
   * @param isbn an ISBN
   * @return a formatted ISBN
   */
  public static String formatIssn(String issn) {
    if (issn != null) {
      String fmtIssn = issn.replaceAll("-", "");
      if (fmtIssn.length() == 8) {
        return  fmtIssn.substring(0,4) + "-" + fmtIssn.substring(4);
      }
    }
    
    return issn;
  }  

  /**
   * Check that the ISBN is a valid. The method validates both ISBN-10 and ISBN-13,
   * with or without punctuation. Checksum is not verified.
   * @param isbn the ISBN string
   * @return true if ISBN is valid, false otherwise
   */
  public static boolean isIsbn(String isbn) {
	  return isIsbn(isbn, false);
  }

  /**
   * Returns the ISBN-13 checksum between 1 and 10 of the first 12
   * characters of the iput, or -1 if input is not a valid unpunctuated 
   * ISBN-13 string.
   * 
   * @param isbn13 unpunctuated ISBN-13 string
   * @return ISBN-13 checksum digit between 0 and 9, or -1 if input not valid
   */
  static private int isbn13CheckSum(String isbn13) {
    // see http://en.wikipedia.org/wiki/Isbn
    int a = 0, b = 1;
    for (int i = 0; i < 12; i++, b = 4-b) {
      int digit = "0123456789".indexOf(isbn13.charAt(i));
      if (digit < 0) {
        return -1;
      }
      a += digit * b;
    }
    
    // checksum + remainder == 10
    return 10 - a % 10;

  }
  
  
  /**
   * Returns the ISBN-10 checksum between 0 and 10 of the first 9
   * characters of the input, or -1 if input is not a valid unpunctuated 
   * ISBN-10 string.
   * 
   * @param isbn10 unpunctuated ISBN-10 string
   * @return ISBN-10 checksum digit between 0 and 11, or -1 if input not valid
   */
  static private int isbn10Checksum(String isbn10) {
    // see http://en.wikipedia.org/wiki/Isbn
    int a = 0, i = 0;
    while (i < 9) {
      int digit = "0123456789".indexOf(isbn10.charAt(i));
      if (digit < 0) {
        return -1;
      }
      a += ++i * digit;
    }
    
    // checksum + remainder == 11
    return a % 11;
  }

  /**
   * Check that the ISBN is a valid.The method validates both ISBN-10 and 
   * ISBN-13, with or without punctuation. Uses techniques described in the 
   * ISBN Wikipedia article
   * <p>
   * <strong>Note:</strong> Due to errors at a publishing house that go 
   * undetected, books have been issued with invalid ISBNs (e.g. 0-85883-554-4).
   * If strict flag is true, verifies checksum, otherwise just verifies form.
   * 
   * @param isbn the ISBN string
   * @param strict if true, also verify checksum, otherwise just check form
   * @return true if ISBN is valid, false otherwise
   */
  public static boolean isIsbn(String isbn, boolean strict) {
    if (isbn == null) {
      return false;
    }
    
    String checkISBN = isbn.replaceAll("-", "");
    if (checkISBN.length() == 10) {
      int checksum = isbn10Checksum(checkISBN); // 0..10
      if (checksum >= 0) {
        int cksum = "0123456789X".indexOf(checkISBN.charAt(9));
        return (cksum >= 0) && (strict ? (checksum == cksum) : true); 
      }
    } else if (checkISBN.length() == 13) {
      int checksum = isbn13CheckSum(checkISBN); // 1..10
      if (checksum >= 0) {
        int cksum = "1234567890".indexOf(checkISBN.charAt(12)) + 1; // 1..10
        return (cksum > 0) && (strict ? (checksum == cksum) : true); 
      }
    }
    
    return false;
  }

  /**
   * Returns the ISBN-13 version of the input ISBN-10 or ISBN-13
   * string. The returned ISBN-13 string has no punctuation and a
   * valid check digit. Assumes ISBN-10 is a book and adds "978" 
   * EAN to create the ISBN-13 string.
   *  
   * @param isbn punctuated or unpunctuated ISBN-10 or ISBN13 string
   * @return unpunctuated ISBN-13 string with proper checksum, or 
   *   <code>null</code> if input string (ignoring checksum) is not a 
   *   possible ISBN-10 or ISBN-13 string
   */
  public static String toIsbn13(String isbn) {
    String isbn13 = isbn.replaceAll("-", "");
    if (isIsbn(isbn13, false)) {
      if (isbn13.length() == 10) {
        // add GS1 book EAN
        isbn13 = "978" + isbn13;
      }
  
      if (isbn13.length() == 13) {
        int checksum = isbn13CheckSum(isbn13); // 1..10
        return (checksum < 0) ? null 
                 : isbn13.substring(0,12) + "1234567890".charAt(checksum-1);
      }
    }
    return null;
  }
  
  /**
   * Returns the ISBN-10 version of the input ISBN-10 or ISBN-13 
   * string. The returned ISBN-10 string has no punctuation and a
   * valid check digit. Recognizes both "978" and "979" ISSN-13 EANs
   *  
   * @param isbn punctuated or unpunctuated ISBN-10 or ISBN13 string
   * @return unpunctuated ISBN-13 string with proper checksum, or 
   *   <code>null</code> if input string (ignoring checksum) is not a 
   *   possible ISBN-10 or ISBN-13 string
   */
  public static String toIsbn10(String isbn) {
    String isbn10 = isbn.replaceAll("-", "");
    if (isIsbn(isbn10, false)) {
      if (isbn10.length() == 13) {
        if (   isbn10.startsWith("978")
            || isbn10.startsWith("979")) {
          // remove GS1 book prefix;
          isbn10 = isbn10.substring(3);
        }
      }
  
      if (isbn10.length() == 10) {
        int checksum = isbn10Checksum(isbn10);
        return (checksum < 0) ? null 
               : isbn10.substring(0,9) + "0123456789X".charAt(checksum);
      }
    }
    return null;
  }
  
  /**
   * Return formatted form if ISBN with hyphens. This method
   * does not check for the vaildity of the input ISBN.
   * 
   * @param isbn an ISBN
   * @return a formatted ISBN
   */
  public static String formatIsbn(String isbn) {
    if (isbn != null) {
      String fmtIsbn = isbn.replaceAll("-", "");
      if (fmtIsbn.length() == 10) {
        return    fmtIsbn.substring(0,1) + "-"
                + fmtIsbn.substring(1,4) + "-"
                + fmtIsbn.substring(4,9) + "-"
                + fmtIsbn.substring(9);
        
      } else if (fmtIsbn.length() == 13) {
        return   fmtIsbn.substring(0,3) + "-" 
               + fmtIsbn.substring(3,4) + "-"
               + fmtIsbn.substring(4,7) + "-"
               + fmtIsbn.substring(7,12) + "-"
               + fmtIsbn.substring(12);
      }
    }

    return isbn;
  }
  
  private static Pattern DOI_PAT = Pattern.compile("10\\.\\d{4}/.*");

  /**
   * Check that DOI number is a valid DOI string. 
   * @param doi the DOI string
   * @return true if DOI is a valid string, false otherwise
   * @deprecated renamed to {@link #isDoi(String doi)}
   */
  @Deprecated
  public static boolean isDOI(String doi) {
    return isDoi(doi);
  }
  
  /**
   * Check that DOI number is a valid DOI string. 
   * @param doi the DOI string
   * @return true if DOI is a valid string, false otherwise
   */
  public static boolean isDoi(String doi) {    

    if (doi == null) {
      return false;
    }
    Matcher m = DOI_PAT.matcher(doi);

    if(!m.matches()){
      return false;
    }
    return true;
  }  

  /////////////////////////////////////////////////////////////////
  //
  // Temporary static methods and global data to collect and search
  // metadata, supporting metadata-based access to content.
  //
  // To be replaced by MatadataManager.
  //
  /////////////////////////////////////////////////////////////////

  public static final String PREFIX = Configuration.PREFIX + "metadata.";


  /** The default Locale in which dates, etc. in metadata should be
   * interpreted, if the plugin doesn't otherwise specify one.  Value is
   * string of the form <tt><i>ll_CC_VVV</i></tt>, where <tt><i>ll</i></tt>
   * is the two letter langueage, <tt><i>CC</i></tt> is the optional two
   * letter country and <tt><i>VVV</i></tt> is the option variant. */
  public static final String PARAM_DEFAULT_LOCALE = PREFIX + "defaultLocale";
  public static final Locale DEFAULT_DEFAULT_LOCALE = Locale.US;

  public static final String PARAM_DOIMAP = PREFIX + "doimap";
  public static final String DEFAULT_DOIMAP = "doi";
  public static final String PARAM_DOI_ENABLE = PREFIX + "doi_enable";
  public static final Boolean DEFAULT_DOI_ENABLE = false;
  public static final String PARAM_OPENURLMAP = PREFIX + "openurlmap";
  public static final String DEFAULT_OPENURLMAP = "openurl";
  public static final String PARAM_OPENURL_ENABLE = PREFIX + "openurl_enable";
  public static final Boolean DEFAULT_OPENURL_ENABLE = false;

  private static Locale defaultLocale = DEFAULT_DEFAULT_LOCALE;

  /** Called by org.lockss.config.MiscConfig
   */
  public static void setConfig(Configuration config,
			       Configuration oldConfig,
			       Configuration.Differences diffs) {
    if (diffs.contains(PREFIX)) {
      setDefaultMetadataLocale(config);
    }
  }

  static void setDefaultMetadataLocale(Configuration config) {
    String lstr = config.get(PARAM_DEFAULT_LOCALE, "");
    lstr = lstr.trim();
    if (StringUtil.isNullString(lstr)) {
      defaultLocale = DEFAULT_DEFAULT_LOCALE;
      return;
    }
    try {
      Locale loc = LocaleUtils.toLocale(lstr);
      Locale goodLoc = findClosestAvailableLocale(loc);
      if (goodLoc != null) {
	if (goodLoc.equals(loc)) {
	  log.debug("Requested Locale: " + loc +
		    " not found, using closest match: " + goodLoc);
	}
	defaultLocale = goodLoc;
      } else {
	log.error("Unknown Locale: " + loc +
		  ", using default locale: " + DEFAULT_DEFAULT_LOCALE);
	defaultLocale = DEFAULT_DEFAULT_LOCALE;
      }
    } catch (IllegalArgumentException e) {
      log.error("Illegal Locale spec: " + lstr + ", " + e.getMessage());
      defaultLocale = DEFAULT_DEFAULT_LOCALE;
    }
  }

  /** Return the closest matching available Locale, or null if none.  Looks
   * for an exact match, then an exact match after dropping the variant (if
   * any), then after dropping the country (if any).
   * @param targetLocale the target Locale
   * @return the closest matching Locale or null
   */
  public static Locale findClosestAvailableLocale(Locale targetLocale) {
    return findClosestLocale(targetLocale,
			     (Set<Locale>)LocaleUtils.availableLocaleSet());
  }

  /** Return the closest matching Locale in the set, or null if none. Looks
   * for an exact match, then an exact match after dropping the variant (if
   * any), then after dropping the country (if any).
   * @param targetLocale the target Locale
   * @param locales the list of Locales in which to search
   * @return the closest matching Locale or null
   */
  public static Locale findClosestLocale(Locale targetLocale,
					 Set<Locale> locales) {
    List<Locale> search = LocaleUtils.localeLookupList(targetLocale);
    for (Locale locale : search) {
      if (locales.contains(locale)) {
	return locale;
      }
    }
    return null;
  }

  /** Return the default Locale to use when interpreting metadata fields */
  public static Locale getDefaultLocale() {
    return defaultLocale;
  }

  // XXX maps should persist across daemon restart
  // XXX should lookup DOI prefix to get map in which to look up suffix
  private static CIProperties doiMap = null;
  // XXX should lookup ISSN to get map in which to look up rest of
  // XXX OpenURL metadata
  private static CIProperties openUrlMap = null;

  private static void initDoiMap() {
    Configuration config = CurrentConfig.getCurrentConfig();
    if (!config.getBoolean(PARAM_DOI_ENABLE, DEFAULT_DOI_ENABLE)) {
      return;
    }
    if (doiMap == null) {
      String doiFileName = config.get(PARAM_DOIMAP, DEFAULT_DOIMAP);
      log.debug("initDoiMap(" + doiFileName + ")");
      File doiFile = new File(doiFileName);
      if (doiFile.exists()) {
	FileInputStream fis = null;
	try {
	  fis = new FileInputStream(doiFile);
	  if (fis != null) {
	    doiMap = new CIProperties();
	    doiMap.load(fis);
	  }
	} catch (IOException ex) {
	  log.error(doiFile + " threw " + ex);
	}
      } else {
	// There isn't a cached DOI map - create one
	// XXX this isn't feasible in production because it
	// XXX would take too long and the map would be way
	// XXX too big, but it is OK for a demo.
	doiMap = createDoiMap();
	if (doiMap != null) {
	  FileOutputStream fos = null;
	  try {
	    fos = new FileOutputStream(new File(doiFileName));
	    if (fos != null) {
	      doiMap.store(fos, "Doi Map");
	    }
	  } catch (IOException ex) {
	    log.error(doiFileName + " threw " + ex);
	  } finally {
	    IOUtil.safeClose(fos);
	  }
	}
      }
    }
  }


  protected static CIProperties createDoiMap() {
    PluginManager pluginMgr = LockssDaemon.getLockssDaemon().getPluginManager();

    final CIProperties ret = new CIProperties();
    ArticleMetadataExtractor.Emitter emitter =
      new ArticleMetadataExtractor.Emitter() {
	public void emitMetadata(ArticleFiles af,
				 ArticleMetadata md) {
	  if (md != null) {
	    CachedUrl cu = af.getFullTextCu();
	    String doi = md.get(MetadataField.FIELD_DOI);
	    if (doi != null) {
	      ret.put(doi, cu.getUrl());
	    } else {
	      log.warning(cu.getUrl() + " has no DOI ");
	    }
	  }      
	}
      };

    for (ArchivalUnit au : pluginMgr.getAllAus()) {
      if (pluginMgr.isInternalAu(au)) {
	continue;
      }
      ArticleMetadataExtractor mdExtractor =
	au.getPlugin().getArticleMetadataExtractor(MetadataTarget.DOI, au);
      for (Iterator<ArticleFiles> iter = au.getArticleIterator();
	   iter.hasNext(); ) {
	ArticleFiles af = iter.next();
	CachedUrl cu = af.getFullTextCu();
	try {
	  if (cu.hasContent()) {
	    mdExtractor.extract(MetadataTarget.DOI, af, emitter);
	  }
	} catch (IOException e) {
	  log.warning("createDoiMap() threw " + e);
	} catch (PluginException e) {
	  log.warning("createDoiMap() threw " + e);
	} finally {
	  AuUtil.safeRelease(cu);
	}
      }
    }
    return ret;
  }

  private static void initOpenUrlMap() {
    Configuration config = CurrentConfig.getCurrentConfig();
    if (!config.getBoolean(PARAM_OPENURL_ENABLE, DEFAULT_OPENURL_ENABLE)) {
      return;
    }
    if (openUrlMap == null) {
      String openUrlFileName = config.get(PARAM_OPENURLMAP, DEFAULT_OPENURLMAP);
      log.debug("initOpenUrlMap(" + openUrlFileName + ")");
      File openUrlFile = new File(openUrlFileName);
      if (openUrlFile.exists()) {
	FileInputStream fis = null;
	try {
	  fis = new FileInputStream(openUrlFile);
	  if (fis != null) {
	    // There is a cached OpenURL map
	    openUrlMap = new CIProperties();
	    openUrlMap.load(fis);
	  }
	} catch (IOException ex) {
	  log.error(openUrlFileName + " threw " + ex);
	} finally {
	  IOUtil.safeClose(fis);
	}
      } else {
	// There isn't a cached OpenURL map - create one
	// XXX this isn't feasible in production because it
	// XXX would take too long and the map would be way
	// XXX too big, but it is OK for a demo.
	openUrlMap = createOpenUrlMap();
	if (openUrlMap != null) {
	  FileOutputStream fos = null;
	  try {
	    fos = new FileOutputStream(new File(openUrlFileName));
	    if (fos != null) {
	      openUrlMap.store(fos, "OpenURL Map");
	    }
	  } catch (IOException ex) {
	    log.error(openUrlFileName + " threw " + ex);
	  } finally {
	    IOUtil.safeClose(fos);
	  }
	}
      }
    }
  }

  protected static CIProperties createOpenUrlMap() {
    PluginManager pluginMgr = LockssDaemon.getLockssDaemon().getPluginManager();

    final CIProperties ret = new CIProperties();
    ArticleMetadataExtractor.Emitter emitter =
      new ArticleMetadataExtractor.Emitter() {
	public void emitMetadata(ArticleFiles af,
				 ArticleMetadata md) {
	  if (md != null) {
	    CachedUrl cu = af.getFullTextCu();
	    // Key for OpenURL map is
	    // issn + "/" + volume + "/" + issue + "/" + spage
	    String issn = md.get(MetadataField.FIELD_ISSN);
	    String volume = md.get(FIELD_VOLUME);
	    String issue = md.get(FIELD_ISSUE);
	    String spage = md.get(MetadataField.FIELD_START_PAGE);
	    if (issn != null && volume != null &&
		issue != null && spage != null) {
	      String key = issn + "/" + volume + "/" + issue + "/" + spage;
	      ret.put(key, cu.getUrl());
	    } else {
	      log.warning(cu.getUrl() + " has content but bad metadata " +
			  (issn == null ? "null" : issn) + "/" +
			  (volume == null ? "null" : volume) + "/" +
			  (issue == null ? "null" : issue) + "/" +
			  (spage == null ? "null" : spage));
	    }
	  }      
	}
      };
    for (ArchivalUnit au : pluginMgr.getAllAus()) {
      if (pluginMgr.isInternalAu(au)) {
	continue;
      }
      ArticleMetadataExtractor mdExtractor =
	au.getPlugin().getArticleMetadataExtractor(MetadataTarget.OpenURL, au);
      for (Iterator<ArticleFiles> iter = au.getArticleIterator();
	   iter.hasNext(); ) {
	ArticleFiles af = iter.next();
	CachedUrl cu = af.getFullTextCu();
	try {
	  if (cu.hasContent()) {
	    mdExtractor.extract(MetadataTarget.OpenURL, af, emitter);
	  }
	} catch (IOException e) {
	  log.warning("createOpenUrlMap() threw " + e);
	} catch (PluginException e) {
	  log.warning("createOpenUrlMap() threw " + e);
	} finally {
	  AuUtil.safeRelease(cu);
	}
      }
    }
    return ret;
  }

  public static String doiToUrl(String doi) {
    String ret = null;
    if (doiMap == null) {
      initDoiMap();
    }
    if (doiMap != null) {
      ret = doiMap.getProperty(doi);
    }
    log.debug2("doiToUrl(" + doi + ") = " + (ret == null ? "null" : ret));
    return ret;
  }

  public static String openUrlToUrl(String openUrl) {
    String ret = null;
    if (openUrlMap == null) {
      initOpenUrlMap();
    }
    if (openUrlMap != null) {
      ret = openUrlMap.getProperty(openUrl);
    }
    return ret;
  }

  protected static void doiForUrl(String doi, String url) {
    if (doiMap == null) {
      initDoiMap();
    }
    if (doiMap != null) {
      doiMap.setProperty(doi, url);
    }
  }

  protected static void openUrlForUrl(String openUrl, String url) {
    if (openUrlMap == null) {
      initOpenUrlMap();
    }
    if (openUrlMap != null) {
      openUrlMap.setProperty(openUrl, url);
    }
  }

  private static String[] doiResolvers = {
    "http://dx.doi.org/",
  };
  private static String[] openUrlResolvers = {
    "http://www.crossref.org/openurl?",
  };
  // If the URL specifies a publisher's DOI or OpenURL resolver,
  // strip the stuff before the ?, reformat the rest and hand it
  // to the Metadata resolver to get the URL for the content in
  // the cache.
  public static String proxyResolver(String url) {
    String ret = null;
    if (StringUtil.isNullString(url)) {
      return ret;
    }
    log.debug2("proxyResolver(" + url + ")");
    boolean found = false;
    // Is it a DOI resolver URL?
    // XXX should use host part to find plugin, then ask plugin if
    // XXX URL specifies resolver, and if so get it to reformat
    // XXX resolver query and feed to Metadata.
    for (int i = 0; i < doiResolvers.length; i++) {
      if (url.startsWith(doiResolvers[i])) {
	String param = url.substring(doiResolvers[i].length());
	log.debug3("doiResolver: " + url + " doi " + param);
	String newUrl =
	  MetadataUtil.doiToUrl(param);
	if (newUrl != null) {
	  ret = newUrl;
	  found = true;
	}
      }
    }
    if (!found) {
      for (int i = 0; i < openUrlResolvers.length; i++) {
	if (url.startsWith(openUrlResolvers[i])) {
	  // issn/volume/issue/spage
	  String query = url.substring(openUrlResolvers[i].length());
	  log.debug3("openUrlResolver: " + url + " openUrl " + query);
	  if (!StringUtil.isNullString(query)) {
	    String[] params = query.split("&");
	    String issn = null;
	    String volume = null;
	    String issue = null;
	    String spage = null;
	    for (int j = 0; j < params.length; j++) {
	      if (params[j].startsWith("issn=")) {
		issn = params[j].substring(5);
	      }
	      if (params[j].startsWith("volume=")) {
		volume = params[j].substring(7);
	      }
	      if (params[j].startsWith("issue=")) {
		issue = params[j].substring(6);
	      }
	      if (params[j].startsWith("spage=")) {
		spage = params[j].substring(6);
	      }
	    }
	    if (issn != null &&
		volume != null &&
		issue != null &&
		spage != null) {
	      String openUrl = issn + "/" + volume + "/" +
		issue + "/" + spage;
	      log.debug3("openUrl: " + openUrl);
	      String newUrl =
		MetadataUtil.openUrlToUrl(openUrl);
	      if (newUrl != null) {
		ret = newUrl;
		found = true;
	      }
	    }
	  }
	}
      }
    }
    log.debug2("proxyResolver returns " + ret);
    return ret;
  }
  /**
   * Check that the author field is valid or at least has a letter.
   * @param keyAuthor the author string
   * @return true if author has a letter at least, false otherwise
   */
  public static boolean isAuthor(String author) {
    if (author == null) {
      return false;
    }
    Matcher m = AUTHOR_PAT.matcher(author);
    return (m.matches());
  }
}