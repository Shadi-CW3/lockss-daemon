/*

Copyright (c) 2000-2020, Board of Trustees of Leland Stanford Jr. University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.plugin.respediatrica;

import org.lockss.daemon.ConfigParamDescr;
import org.lockss.extractor.GoslingHtmlLinkExtractor;
import org.lockss.extractor.LinkExtractor;
import org.lockss.extractor.LinkExtractorFactory;
import org.lockss.plugin.ArchivalUnit;
import org.lockss.util.Logger;
import org.lockss.util.UrlUtil;

import java.io.IOException;
import java.io.InputStream;

public class ResPediatricaHtmlLinkExtractor extends GoslingHtmlLinkExtractor {
  
  private static final Logger logger = Logger.getLogger(ResPediatricaHtmlLinkExtractor.class);
  
  @Override
  public void extractUrls(final ArchivalUnit au,
                          InputStream in,
                          String encoding,
                          final String srcUrl,
                          final Callback cb)
      throws IOException {
    
    String url = UrlUtil.stripProtocol(au.getConfiguration().get(ConfigParamDescr.BASE_URL.getKey()));
    // Do NOT extract links from home page, as this lead to 
    // over-crawling from current article links on that page
    // The journal home page is the permission page, so it is needed
    if (srcUrl.endsWith(url)) {
      return;
    }
    super.extractUrls(au, in, encoding, srcUrl, cb);
  }
  
  public static class Factory implements LinkExtractorFactory {
    public LinkExtractor createLinkExtractor(String mimeType) {
      return new ResPediatricaHtmlLinkExtractor();
    }
  }
  
}
