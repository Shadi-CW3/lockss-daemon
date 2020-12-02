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

package org.lockss.plugin.ubiquitypress.upn;

import java.io.*;

import org.lockss.daemon.PluginException;
import org.lockss.plugin.ArchivalUnit;
import org.lockss.rewriter.*;
import org.lockss.servlet.ServletUtil.LinkTransform;

public class UbiquityPartnerNetworkHtmlLinkRewriterFactory implements LinkRewriterFactory {

  @Override
  public InputStream createLinkRewriter(String mimeType, 
                                        ArchivalUnit au,
                                        InputStream in,
                                        String encoding,
                                        String url,
                                        LinkTransform xform)
      throws PluginException, IOException {
    NodeFilterHtmlLinkRewriterFactory fact = new NodeFilterHtmlLinkRewriterFactory();
    
    /*
     * Images in Utrecht University Library's Studium
     * (https://www.gewina-studium.nl/) are displayed in a Featherlight
     * (https://noelboss.github.io/featherlight/) widget. In this context, an
     * <a> tag has a 'data-featherlight' attribute with a relative URL, and
     * contains a regular <img> tag inside it. When clicked, the image is
     * rendered in a popup, but using the 'data-featherlight' link from the <a>
     * tag.
     * 
     * Example from
     * https://www.gewina-studium.nl/articles/10.18352/studium.10120/:
     * 
     * <a href="#" data-featherlight="figures/Cocquyt_fig1.jpg"><img
     * src="figures/Cocquyt_fig1.jpg"></a>
     */
    fact.addAttrToRewrite("data-featherlight");
    
    return fact.createLinkRewriter(mimeType, au, in, encoding, url, xform);
  }
  
}
