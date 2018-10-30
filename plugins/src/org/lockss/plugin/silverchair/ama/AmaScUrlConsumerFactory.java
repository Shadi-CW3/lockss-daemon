/*
 * $Id$
 */

/*

Copyright (c) 2018 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.plugin.silverchair.ama;

import java.util.regex.Pattern;

import org.lockss.daemon.Crawler.CrawlerFacade;
import org.lockss.plugin.*;
import org.lockss.plugin.base.HttpToHttpsUrlConsumer;
import org.lockss.util.Logger;

/**
 * @since 1.70.0 with storeAtOrigUrl() and https transition support
 */
public class AmaScUrlConsumerFactory implements UrlConsumerFactory {
  private static final Logger log = Logger.getLogger(AmaScUrlConsumerFactory.class);

  protected static final String PDF_STRING = "/data/journals/[^/]+/[^/]+/[^/.]+[.]pdf";
  protected static final String ORIG_PDF_STRING = PDF_STRING + "$";
  protected static final String DEST_PDF_STRING =
      "/pdfaccess[.]ashx[?]url=" + PDF_STRING + "(.routename=.+)?$";
  
  // 4/13/18 - the website has changed - now using https AND the urls and redirects have changed again
  // PDF links are: https://jamanetwork.com/journals/jamacardiology/articlepdf/2569804/hoi160069.pdf
  // no longer appear to redirect, nor do figures, tables, etc
  // PDF supplemental files redirect 
  // from: https://jamanetwork.com/data/Journals/CARDIOLOGY/935934/HOI160069supp1_prod.pdf
  // to: https://jamanetwork.com/journals/CARDIOLOGY/articlepdf/2569804/hoi160069supp1_prod.pdf
  protected static final String NEW_PDF_STRING = "/journals/[^/]+/articlepdf/[^/]+/[^/.]+[.]pdf";
    
  protected static final Pattern origPdfPat = Pattern.compile(ORIG_PDF_STRING, Pattern.CASE_INSENSITIVE);
  protected static final Pattern destPdfPat = Pattern.compile(DEST_PDF_STRING, Pattern.CASE_INSENSITIVE);
  protected static final Pattern newDestPdfPat = Pattern.compile(NEW_PDF_STRING, Pattern.CASE_INSENSITIVE);
  
  @Override
  public UrlConsumer createUrlConsumer(CrawlerFacade facade, FetchedUrlData fud) {
    log.debug3("Creating a UrlConsumer");
    return new AmaScUrlConsumer(facade, fud);
  }
  
  public static Pattern getOrigPdfPattern() {
    return origPdfPat;
  }
  
  public static Pattern getDestPdfPattern() {
    return destPdfPat;
  }
  
  /**
   * <p>
   * A custom URL consumer that identifies specific redirect chains and stores the
   * content at the origin of the chain 
   * 
   * The article PDFs are redirected to a url with pdfaccess.ashx?url=
   * So while the link will look like this:
   *   http://jamanetwork.com/journals/jamainternalmedicine/data/journals/intemed/935149/ioi150114.pdf
   * it will end up here:
   *   http://jamanetwork.com/pdfaccess.ashx?url=/data/journals/intemed/935149/ioi150114.pdf&routename=jamainternalmedicine
   * 
   * Another example is
   *    http://jamanetwork.com/data/Journals/INTEMED/935149/IOI150114supp1_prod.pdf
   * redirects to
   *    http://jamanetwork.com/pdfaccess.ashx?url=/data/journals/intemed/935149/ioi150114supp1_prod.pdf
   * </p>
   * 
   * @since 1.68.0
   */
  public class AmaScUrlConsumer extends HttpToHttpsUrlConsumer {
    
    public AmaScUrlConsumer(CrawlerFacade facade,
        FetchedUrlData fud) {
      super(facade, fud);
    }
    
    
    /**
     * <p>
     * Determines if a particular redirect chain should cause content to be stored
     * only at the origin URL ({@link FetchedUrlData#origUrl}).
     * </p>
     * 
     * @return True if and only if the fetched URL data represents a particular
     *         redirect chain that should cause content to be stored only at the
     *         origin URL.
     *         Also support for http to https transition
     */
    @Override
	public boolean shouldStoreAtOrigUrl() {
     	// first just check for simple http to https transition 
    	  boolean should = super.shouldStoreAtOrigUrl();
      if ((!should) && (fud.redirectUrls != null && fud.redirectUrls.size() >=1)) { // could be http to https to dest
  	  	should =  (
    	          (destPdfPat.matcher(fud.fetchUrl).find() && origPdfPat.matcher(fud.origUrl).find()) ||
    	          // 2nd generation - original could be either; fetched has articlepdf
    	          (newDestPdfPat.matcher(fud.fetchUrl).find() &&
    	      	     (origPdfPat.matcher(fud.origUrl).find() || newDestPdfPat.matcher(fud.origUrl).find()))
    	      	  );
      }
      return should;
    }
    
  }
  
}
