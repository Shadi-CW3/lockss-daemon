/*
 * $Id:$
 */

/*

Copyright (c) 2000-2019 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.plugin.springer.link;

import org.lockss.daemon.PluginException;
import org.lockss.plugin.*;
import org.lockss.util.Logger;
import org.lockss.util.urlconn.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringerLinkHttpResponseHandler implements CacheResultHandler {

  private static final Logger logger = Logger.getLogger(SpringerLinkHttpResponseHandler.class);
  protected static final Pattern NON_FATAL_PAT =
      Pattern.compile("^http(s)?://static-content.springer.com/(esm|image)?/art[^/]+/MediaObjects/");

  @Override
  public void init(CacheResultMap crmap) {
    logger.warning("Unexpected call to init()");
    throw new UnsupportedOperationException("Unexpected call to HighWirePressHttpResponseHandler.init()");
  }

  @Override
  public CacheException handleResult(ArchivalUnit au,
                                     String url,
                                     int responseCode)
      throws PluginException {
    logger.debug2(String.format("URL %s with %d", url, responseCode));
    switch (responseCode) {
      case 403:
        logger.debug3("403");
        Matcher mat = NON_FATAL_PAT.matcher(url);
        if (mat.find()) {
            return new CacheException.NoRetryDeadLinkException("403 Forbidden (non-fatal)");
        } else {
            return new CacheException.NoRetryDeadLinkException("403 Forbidden error");
        }
      case 404:
        logger.debug3("404");
        if(url.contains("MediaObjects")) {
        	return new SpringerLinkRetryDeadLinkException("404 Not Found");
        }
        return new CacheException.RetryDeadLinkException("404 Not Found");
      case 500:
        logger.debug3("500");
        return new CacheException.RetrySameUrlException("500 Internal Server Error");
      default:
        logger.warning("Unexpected responseCode (" + responseCode + ") in handleResult(): AU " + au.getName() + "; URL " + url);
        throw new UnsupportedOperationException("Unexpected responseCode (" + responseCode + ")");
    }
  }

  @Override
  public CacheException handleResult(ArchivalUnit au,
                                     String url,
                                     Exception ex)
      throws PluginException {
    logger.debug2(String.format("URL %s with %s", url, ex.getClass().getName()));
    if (ex instanceof ContentValidationException.WrongLength) {
      logger.debug3("Wrong length");
      return new SpringerLinkRetryDeadLinkException(ex.getMessage());
    }
    logger.warning("Unexpected error type (" + ex.getClass().getName() + ") in handleResult(): AU " + au.getName() + "; URL " + url);
    throw new UnsupportedOperationException("Unexpected error type", ex);
  }
  
  class SpringerLinkRetryDeadLinkException extends CacheException.RetryDeadLinkException {
    
    public SpringerLinkRetryDeadLinkException() {
      super();
    }

    public SpringerLinkRetryDeadLinkException(String message) {
      super(message);
    }

    public int getRetryCount() {
      return 7;
    }
    
  }
  
}
