/*

Copyright (c) 2000-2023, Board of Trustees of Leland Stanford Jr. University

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

package org.lockss.plugin.emls;

import java.net.*;
import java.util.*;
import org.lockss.test.*;
import org.lockss.config.Configuration;
import org.lockss.daemon.*;
import org.lockss.plugin.ArchivalUnit;
import org.lockss.plugin.definable.*;

public class TestEmlsPlugin extends LockssTestCase {
  private DefinablePlugin plugin;
  static final String BASE_URL_KEY = ConfigParamDescr.BASE_URL.getKey();
  static final String VOL_KEY = ConfigParamDescr.VOLUME_NUMBER.getKey();

  public void setUp() throws Exception {
    super.setUp();
    plugin = new DefinablePlugin();
    plugin.initPlugin(getMockLockssDaemon(),
                      "org.lockss.plugin.emls.EmlsPlugin");
  }

  public void testGetAuNullConfig() throws ArchivalUnit.ConfigurationException {
    try {
      plugin.configureAu(null, null);
      fail("Didn't throw ArchivalUnit.ConfigurationException");
    } catch (ArchivalUnit.ConfigurationException e) { }
  }

  private DefinableArchivalUnit makeAuFromProps(Properties props)
      throws ArchivalUnit.ConfigurationException {
    Configuration config = ConfigurationUtil.fromProps(props);
    return (DefinableArchivalUnit)plugin.configureAu(config, null);
  }

  public void testGetAuHandlesBadUrl()
      throws ArchivalUnit.ConfigurationException, MalformedURLException {
    Properties props = new Properties();
    props.setProperty(BASE_URL_KEY, "blah");
    props.setProperty(VOL_KEY, "3");

    try {
      DefinableArchivalUnit au = makeAuFromProps(props);
      fail ("Didn't throw InstantiationException when given a bad url");
    } catch (ArchivalUnit.ConfigurationException auie) {
      ConfigParamDescr.InvalidFormatException murle =
        (ConfigParamDescr.InvalidFormatException)auie.getCause();
      assertNotNull(auie.getCause());
    }
  }

  public void testGetAuConstructsProperAu()
      throws ArchivalUnit.ConfigurationException, MalformedURLException {
    Properties props = new Properties();
    props.setProperty(BASE_URL_KEY,
                      "http://extra.shu.ac.uk/emls/");
    props.setProperty(VOL_KEY, "3");

    DefinableArchivalUnit au = makeAuFromProps(props);
    assertEquals("Early Modern Literary Studies Plugin, Base URL http://extra.shu.ac.uk/emls/, Volume 3", au.getName());
  }

  public void testGetPluginId() {
    assertEquals("org.lockss.plugin.emls.EmlsPlugin",
                 plugin.getPluginId());
  }

  public void testGetAuConfigProperties() {
    for (Iterator iter = plugin.getLocalAuConfigDescrs().iterator() ; iter.hasNext() ; ) {
      ConfigParamDescr desc = (ConfigParamDescr)iter.next();
      if (desc.equals(ConfigParamDescr.BASE_URL)) { continue; }
      if (desc.equals(ConfigParamDescr.VOLUME_NUMBER)) { continue; }
      if ("issues".equals(desc.getKey())) {
        assertEquals(ConfigParamDescr.TYPE_SET, desc.getType());
        assertFalse(desc.isDefinitional());
        continue;
      }
      fail("Unexpected config param: " + desc.getKey());
    }
  }
}
