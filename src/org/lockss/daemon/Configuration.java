/*
 * $Id: Configuration.java,v 1.49 2003-07-11 23:29:58 tlipkis Exp $
 */

/*

Copyright (c) 2001-2003 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.daemon;

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;
import org.mortbay.tools.*;
import org.lockss.util.*;
import org.lockss.servlet.*;
import org.lockss.proxy.*;
import org.lockss.protocol.*;
import org.lockss.repository.*;
import org.lockss.state.*;

/** <code>Configuration</code> provides access to the LOCKSS configuration
 * parameters.  Instances of (concrete subclasses of)
 * <code>Configuration</code> hold a set of configuration parameters, and
 * have a standard set of accessors.  Static methods on this class provide
 * convenient access to parameter values in the "current" configuration;
 * these accessors all have <code>Param</code> in their name.  (If called
 * on a <code>Configuration</code> <i>instance</i>, they will return values
 * from the current configuration, not that instance.  So don't do that.)
 */
public abstract class Configuration {
  /** A constant empty Configuration object */
  public static final Configuration EMPTY_CONFIGURATION = newConfiguration();

  /** The common prefix string of all LOCKSS configuration parameters. */
  public static final String PREFIX = "org.lockss.";

  static final String MYPREFIX = PREFIX + "config.";
  static final String PARAM_RELOAD_INTERVAL = MYPREFIX + "reloadInterval";
  static final long DEFAULT_RELOAD_INTERVAL = 30 * Constants.MINUTE;
  static final String PARAM_CONFIG_PATH = MYPREFIX + "configFilePath";
  static final String DEFAULT_CONFIG_PATH = "config";

  /** Common prefix of platform config params */
  static final String PLATFORM = PREFIX + "platform.";

  /** Local (routable) IP address, for lcap identity */
  public static final String PARAM_PLATFORM_IP_ADDRESS =
    PLATFORM + "localIPAddress";

  /** Local subnet set during config */
  public static final String PARAM_PLATFORM_ACCESS_SUBNET =
    PLATFORM + "accesssubnet";

  static final String PARAM_PLATFORM_DISK_SPACE_LIST =
    PLATFORM + "diskSpacePaths";

  static final String PARAM_PLATFORM_VERSION = PLATFORM + "version";
  static final String PARAM_PLATFORM_ADMIN_EMAIL = PLATFORM + "sysadminemail";
  static final String PARAM_PLATFORM_LOG_DIR = PLATFORM + "logdirectory";
  static final String PARAM_PLATFORM_LOG_FILE = PLATFORM + "logfile";

  static final String PARAM_PLATFORM_SMTP_HOST = PLATFORM + "smtphost";
  static final String PARAM_PLATFORM_SMTP_PORT = PLATFORM + "smtpport";
  static final String PARAM_PLATFORM_PIDFILE = PLATFORM + "pidfile";

  public static String CONFIG_FILE_UI_IP_ACCESS = "ui_ip_access.txt";

  /** array of local cache config file names */
  static String cacheConfigFiles[] = {
    CONFIG_FILE_UI_IP_ACCESS,
  };


  // MUST pass in explicit log level to avoid recursive call back to
  // Configuration to get Config log level.  (Others should NOT do this.)
  protected static Logger log =
    Logger.getLogger("Config", Logger.getInitialDefaultLevel());

  private static List configChangedCallbacks = new ArrayList();

  private static List configUrlList;	// list of urls

  // Current configuration instance.
  // Start with an empty one to avoid errors in the static accessors.
  private static Configuration currentConfig = newConfiguration();
  private static Configuration emptyConfig = newConfiguration();
  private static OneShotSemaphore haveConfig = new OneShotSemaphore();

  private static HandlerThread handlerThread; // reload handler thread

  private static long reloadInterval = 10 * Constants.MINUTE;


  // Factory to create instance of appropriate class
  static Configuration newConfiguration() {
    return new ConfigurationPropTreeImpl();
  }

  /** Return current configuration */
  public static Configuration getCurrentConfig() {
    return currentConfig;
  }

  /** Reset to unconfigured state.
   * Configuration isn't a service: this isn't a LockssManager.stopService(),
   * but should be made one when Configuration is made a service.  (And see
   * LockssTestCase.tearDown(), where this is called.)
   */
  public static void stopService() {
    currentConfig = newConfiguration();
    // this currently runs afoul of Logger, which registers itself once
    // only, on first use.
    configChangedCallbacks = new ArrayList();
    configUrlList = null;
    stopHandler();
    haveConfig = new OneShotSemaphore();
  }

  /** Wait until the system is configured.  (<i>Ie</i>, until the first
   * time a configuration has been loaded.)
   * @param timer limits the time to wait.  If null, returns immediately.
   * @return true if configured, false if timer expired.
   */
  public static boolean waitConfig(Deadline timer) {
    while (!haveConfig.isFull() && !timer.expired()) {
      try {
	haveConfig.waitFull(timer);
      } catch (InterruptedException e) {
	// no action - check timer
      }
    }
    return haveConfig.isFull();
  }

  /** Wait until the system is configured.  (<i>Ie</i>, until the first
   * time a configuration has been loaded.) */
  public static boolean waitConfig() {
    return waitConfig(Deadline.MAX);
  }

  static void setCurrentConfig(Configuration newConfig) {
    if (newConfig == null) {
      log.warning("attempt to install null Configuration");
    }
    currentConfig = newConfig;
  }

  static void runCallback(Callback cb,
			  Configuration newConfig,
			  Configuration oldConfig,
			  Set diffs) {
    try {
      cb.configurationChanged(newConfig, oldConfig, diffs);
    } catch (Exception e) {
      log.error("callback threw", e);
    }
  }

  static void runCallback(Callback cb,
			  Configuration newConfig,
			  Configuration oldConfig) {
    runCallback(cb, newConfig, oldConfig, newConfig.differentKeys(oldConfig));
  }

  static void runCallbacks(Configuration newConfig,
			   Configuration oldConfig) {
    Set diffs = newConfig.differentKeys(oldConfig);
    // copy the list of callbacks as it could change during the loop.
    List cblist = new ArrayList(configChangedCallbacks);
    for (Iterator iter = cblist.iterator(); iter.hasNext();) {
      try {
	Callback cb = (Callback)iter.next();
	runCallback(cb, newConfig, oldConfig, diffs);
      } catch (RuntimeException e) {
	throw e;
      }
    }
  }

  static void setConfigUrls(List urls) {
    configUrlList = new ArrayList(urls);
  }

  static void setConfigUrls(String urls) {
    configUrlList = new ArrayList();
    for (StringTokenizer st = new StringTokenizer(urls);
	 st.hasMoreElements(); ) {
      String url = st.nextToken();
      configUrlList.add(url);
    }
  }

  /**
   * Return a new <code>Configuration</code> instance loaded from the
   * url list
   */
  public static Configuration readConfig(List urlList) {
    if (urlList == null) {
      return null;
    }
    Configuration newConfig = newConfiguration();
    newConfig.setConfigUrls(urlList);
    boolean gotIt = newConfig.loadList(urlList);
    return gotIt ? newConfig : null;
  }

  static boolean updateConfig() {
    Configuration newConfig = readConfig(configUrlList);
    return installConfig(newConfig);
  }

  static boolean installConfig(Configuration newConfig) {
    if (newConfig == null) {
      return false;
    }
    initCacheConfig(newConfig);
    loadCacheConfigInto(newConfig);
    newConfig.copyPlatformParams();
    newConfig.seal();
    Configuration oldConfig = currentConfig;
    if (!oldConfig.isEmpty() && newConfig.equals(oldConfig)) {
      if (reloadInterval >= 10 * Constants.MINUTE) {
	log.info("Config unchanged, not updated");
      }
      return false;
    }
    setCurrentConfig(newConfig);
    log.info("Config updated from " +
	     StringUtil.separatedString(newConfig.configUrlList, ", "));
    if (log.isDebug()) {
      newConfig.logConfig();
    }
    runCallbacks(newConfig, oldConfig);
    haveConfig.fill();
    return true;
  }

  private void copyPlatformParams() {
    String logdir = get(PARAM_PLATFORM_LOG_DIR);
    String logfile = get(PARAM_PLATFORM_LOG_FILE);
    if (logdir != null && logfile != null) {
      platformOverride(FileTarget.PARAM_FILE,
		       new File(logdir, logfile).toString());
    }

    conditionalPlatformOverride(IdentityManager.PARAM_LOCAL_IP,
				PARAM_PLATFORM_IP_ADDRESS);

    conditionalPlatformOverride(MailTarget.PARAM_SMTPPORT,
				PARAM_PLATFORM_SMTP_PORT);
    conditionalPlatformOverride(MailTarget.PARAM_SMTPHOST,
				PARAM_PLATFORM_SMTP_HOST);

    String platformSubnet = get(PARAM_PLATFORM_ACCESS_SUBNET);
    appendPlatformAccess(ServletManager.PARAM_IP_INCLUDE, platformSubnet);
    appendPlatformAccess(ProxyManager.PARAM_IP_INCLUDE, platformSubnet);

    String space = get(PARAM_PLATFORM_DISK_SPACE_LIST);
    if (!StringUtil.isNullString(space)) {
      String firstSpace =
	((String)StringUtil.breakAt(space, ';', 1).elementAt(0));
      platformOverride(LockssRepositoryImpl.PARAM_CACHE_LOCATION,
		       firstSpace);
      platformOverride(HistoryRepositoryImpl.PARAM_HISTORY_LOCATION,
		       firstSpace);
      platformOverride(IdentityManager.PARAM_IDDB_DIR,
		       new File(firstSpace, "iddb").toString());
    }
  }

  private void platformOverride(String key, String val) {
    if (get(key) != null) {
      log.warning("Overriding param: " + key + "= " + get(key));
      log.warning("with platform-derived value: " + val);
    }
    put(key, val);
  }

  private void conditionalPlatformOverride(String key,
					   String withPlatformKey) {
    String value = get(withPlatformKey);
    if (value != null) {
      platformOverride(key, value);
    }
  }

  private void appendPlatformAccess(String accessParam,
				    String platformAccess) {
    if (StringUtil.isNullString(platformAccess)) {
      return;
    }
    String includeIps = get(accessParam);
    if (StringUtil.isNullString(includeIps)) {
      includeIps = platformAccess;
    } else {
      includeIps = platformAccess + ";" + includeIps;
    }
    put(accessParam, includeIps);
  }


  private void logConfig() {
    SortedSet keys = new TreeSet();
    for (Iterator iter = keyIterator(); iter.hasNext(); ) {
      keys.add((String)iter.next());
    }
    for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
      String key = (String)iter.next();
      log.debug(key + " = " + (String)get(key));
    }
  }

  /**
   * Register a {@link Configuration.Callback}, which will be called
   * whenever the current configuration has changed.  If a configuration is
   * present when a callback is registered, the callback will be called
   * immediately.
   * @param c <code>Configuration.Callback</code> to add.  */
  public static void registerConfigurationCallback(Callback c) {
    log.debug3("registering " + c);
    if (!configChangedCallbacks.contains(c)) {
      configChangedCallbacks.add(c);
      if (haveConfig.isFull()) {
	runCallback(c, currentConfig, emptyConfig);
      }
    }
  }

  /**
   * Unregister a <code>Configuration.Callback</code>.
   * @param c <code>Configuration.Callback</code> to remove.
   */
  public static void unregisterConfigurationCallback(Callback c) {
    log.debug3("unregistering " + c);
    configChangedCallbacks.remove(c);
  }

  // instance methods

  /**
   * Try to load config from a list or urls
   * @return true iff properties were successfully loaded
   */
  boolean loadList(List urls) {
    return loadList(urls, false);
  }

  /**
   * Try to load config from a list or urls
   * @return true iff properties were successfully loaded
   */
  boolean loadList(List urls, boolean failOk) {
    for (Iterator iter = urls.iterator(); iter.hasNext();) {
      String url = (String)iter.next();
      try {
	load(url);
      } catch (IOException e) {
	if (e instanceof FileNotFoundException &&
	    StringUtil.endsWithIgnoreCase(url.toString(), ".opt")) {
	  log.info("Not loading props from nonexistent optional file: " + url);
	} else {
	  // This load failed.  Fail the whole thing.
	  if (!failOk) {
	    log.warning("Couldn't load props from " + url + ": " +
			e.toString());
	    reset();			// ensure config is empty
	  }
	  return false;
	}
      }
    }
    return true;
  }

  void load(String url) throws IOException {
    InputStream istr;
    try {
      istr = UrlUtil.openInputStream(url);
      log.debug2("load URL: " + url);
    } catch (MalformedURLException e) {
      istr = new FileInputStream(url);
      log.debug2("load file: " + url);
    }
    load(new BufferedInputStream(istr));
  }

  abstract boolean load(InputStream istr)
      throws IOException;

  static void resetForTesting() {
    cacheConfigInited = false;
    cacheConfigDir = null;
  }

  static boolean cacheConfigInited = false;
  static String cacheConfigDir = null;

  static boolean isUnitTesting() {
    return Boolean.getBoolean("org.lockss.unitTesting");
  }

  private static void initCacheConfig(Configuration newConfig) {
    if (cacheConfigInited) return;
    String dspace = newConfig.get(PARAM_PLATFORM_DISK_SPACE_LIST);
    String relConfigPath = newConfig.get(PARAM_CONFIG_PATH,
					 DEFAULT_CONFIG_PATH);
    Vector v = StringUtil.breakAt(dspace, ';');
    if (!isUnitTesting() && v.size() == 0) {
      log.error(PARAM_PLATFORM_DISK_SPACE_LIST +
		" not specified, not configuring local cache config dir");
      return;
    }
    for (Iterator iter = v.iterator(); iter.hasNext(); ) {
      String path = (String)iter.next();
      File configDir = new File(path, relConfigPath);
      if (configDir.exists()) {
	cacheConfigDir = configDir.toString();
	break;
      }
    }
    if (cacheConfigDir == null) {
      if (v.size() >= 1) {
	String path = (String)v.get(0);
	File dir = new File(path, relConfigPath);
	if (dir.mkdirs()) {
	  cacheConfigDir = dir.toString();
	}
      }
    }
    cacheConfigInited = true;
  }

//   private void initConfigDir(String path) {
//     String configPath = Configuration.getParam(PARAM_CONFIG_PATH, "config");
//     File dir = new File(path, configPath);
//     if (!dir.exists()) {
//       dir.mkdirs();
//     }
//   }

  static void loadCacheConfigInto(Configuration config) {
    if (cacheConfigDir == null) {
      return;
    }
    for (int ix = 0; ix < cacheConfigFiles.length; ix++) {
      File cfile = new File(cacheConfigDir, cacheConfigFiles[ix]);
      boolean gotIt = config.loadList(ListUtil.list(cfile.toString()), true);
    }
  }

  public static void writeCacheConfigFile(Properties props,
					  String cacheConfigFileName,
					  String header)
      throws IOException {
    if (cacheConfigDir == null) {
      log.warning("Attempting to write cache config file: " +
		  cacheConfigFileName + ", but no cache config dir exists");
      throw new RuntimeException("No cache config dir");
    }
    File cfile = new File(cacheConfigDir, cacheConfigFileName);
    OutputStream os = new FileOutputStream(cfile);
    props.store(os, header);
    os.close();
    if (handlerThread != null) {
      handlerThread.forceReload();
    }
  }

  /** Return the set of keys whose values differ.
   * @param otherConfig the config to compare with.  May be null.
   */
  abstract Set differentKeys(Configuration otherConfig);

  /** Return true iff config has no keys/ */
  public boolean isEmpty() {
    return !(keyIterator().hasNext());
  }

  /** Return the config value associated with <code>key</code>.
   * If the value is null or the key is missing, return <code>dfault</code>.
   */
  public String get(String key, String dfault) {
    String val = get(key);
    if (val == null) {
      val = dfault;
    }
    return val;
  }

  private static Map boolStrings = new HashMap();
  static {
    boolStrings.put("true", Boolean.TRUE);
    boolStrings.put("yes", Boolean.TRUE);
    boolStrings.put("on", Boolean.TRUE);
    boolStrings.put("1", Boolean.TRUE);
    boolStrings.put("false", Boolean.FALSE);
    boolStrings.put("no", Boolean.FALSE);
    boolStrings.put("off", Boolean.FALSE);
    boolStrings.put("0", Boolean.FALSE);
  }

  private Boolean stringToBool(String s) {
    if (s == null) {
      return null;
    }
    Boolean res = (Boolean)boolStrings.get(s);
    if (res != null) {
      return res;
    } else {
      return (Boolean)boolStrings.get(s.toLowerCase());
    }
  }

  /** Return the config value as a boolean.
   * @throws Configuration.InvalidParam if the value is missing or
   * not parsable as a boolean.
   */
  public boolean getBoolean(String key) throws InvalidParam {
    String val = get(key);
    Boolean bool = stringToBool(val);
    if (bool != null) {
      return bool.booleanValue();
    }
    throw new InvalidParam("Not a boolean value: " + key + " = " + val);
  }

  /** Return the config value as a boolean.  If it's missing, return the
   * default value.  If it's present but not parsable as a boolean, log a
   * warning and return the default value.
   */
  public boolean getBoolean(String key, boolean dfault) {
    String val = get(key);
    if (val == null) {
      return dfault;
    }
    Boolean bool = stringToBool(val);
    if (bool != null) {
      return bool.booleanValue();
    }
    log.warning("getBoolean(\'" + key + "\") = \"" + val + "\"");
    return dfault;
  }

  /** Return the config value as an int.
   * @throws Configuration.InvalidParam if the value is missing or
   * not parsable as an int.
   */
  public int getInt(String key) throws InvalidParam {
    String val = get(key);
    try {
      return Integer.parseInt(val);
    } catch (NumberFormatException e) {
      throw new InvalidParam("Not an int value: " + key + " = " + val);
    }
  }

  /** Return the config value as an int.  If it's missing, return the
   * default value.  If it's present but not parsable as an int, log a
   * warning and return the default value
   */
  public int getInt(String key, int dfault) {
    String val = get(key);
    if (val == null) {
      return dfault;
    }
    try {
      return Integer.parseInt(val);
    } catch (NumberFormatException e) {
      log.warning("getInt(\'" + key + "\") = \"" + val + "\"");
      return dfault;
    }
  }

  /** Return the config value as a long.
   * @throws Configuration.InvalidParam if the value is missing or
   * not parsable as a long.
   */
  public long getLong(String key) throws InvalidParam {
    String val = get(key);
    try {
      return Long.parseLong(val);
    } catch (NumberFormatException e) {
      throw new InvalidParam("Not a long value: " + key + " = " + val);
    }
  }

  /** Return the config value as a long.  If it's missing, return the
   * default value.  If it's present but not parsable as a long, log a
   * warning and return the default value
   */
  public long getLong(String key, long dfault) {
    String val = get(key);
    if (val == null) {
      return dfault;
    }
    try {
      return Long.parseLong(val);
    } catch (NumberFormatException e) {
      log.warning("getLong(\'" + key + "\") = \"" + val + "\"");
      return dfault;
    }
  }

  /** Parse the config value as a time interval.  An interval is specified
   * as an integer with an optional suffix.  No suffix means milliseconds,
   * s, m, h, d, w indicates seconds, minutes, hours, days and weeks
   * respectively.
   * @param key the configuration parameter name
   * @return time interval
   * @throws Configuration.InvalidParam if the value is missing or
   * not parsable as a time interval.
   */
  public long getTimeInterval(String key) throws InvalidParam {
    String val = get(key);
    try {
      return StringUtil.parseTimeInterval(val);
    } catch (Exception e) {
      throw new InvalidParam("Not a time interval value: " +
			     key + " = " + val);
    }
  }

  /** Parse the config value as a time interval.  An interval is specified
   * as an integer with an optional suffix.  No suffix means milliseconds,
   * s, m, h, d, w indicates seconds, minutes, hours, days and weeks
   * respectively.  If the parameter is not present, return the
   * default value.  If it's present but not parsable as a long, log a
   * warning and return the default value.
   * @param key the configuration parameter name
   * @param dfault the default value in milliseconds
   * @return time interval
   */
  public long getTimeInterval(String key, long dfault) {
    String val = get(key);
    if (val == null) {
      return dfault;
    }
    try {
      return StringUtil.parseTimeInterval(val);
    } catch (Exception e) {
      log.warning("getTimeInterval(\'" + key + "\") = \"" + val + "\"");
      return dfault;
    }
  }

  /** Parse the config value (which must be an integer between 0 and 100)
   * as a percentage, returning a float between 0.0 and 1.0.
   * @param key the configuration parameter name
   * @return a float between 0.0 and 1.0
   * @throws Configuration.InvalidParam if the value is missing or
   * not an integer between 0 and 100.
   */
  public float getPercentage(String key) throws InvalidParam {
    int val = getInt(key);
    if (val < 0 || val > 100) {
      throw new InvalidParam("Not an integer between 0 and 100: " +
			     key + " = " + val);
    }
    return ((float)val) / (float)100.0;
  }

  /** Parse the config value (which should be an integer between 0 and 100)
   * as a percentage, returning a float between 0.0 and 1.0.  If the
   * parameter is not present, return the default value.  If it's present
   * but not parsable as an int between 0 and 100, log a warning and return
   * the default value.
   * @param key the configuration parameter name
   * @return a float between 0.0 and 1.0
   */
  public float getPercentage(String key, double dfault) {
    int val;
    if (!containsKey(key)) {
      return (float)dfault;
    }
    try {
      val = getInt(key);
    } catch (InvalidParam e) {
      log.warning("getPercentage(\'" + key + "\") = \"" + get(key) + "\"");
      return (float)dfault;
    }
    if (val < 0 || val > 100) {
      log.warning("getPercentage(\'" + key + "\") = \"" + val + "\"");
      return (float)dfault;
    }
    return ((float)val) / 100.0f;
  }

  // must be implemented by implementation subclass

  abstract void reset();

  /** return true iff the configurations have the same keys
   * with the same values.
   */
  public abstract boolean equals(Object c);

  /** Return true if the key is present
   * @param key the key to test
   * @return true if the key is present.
   */
  public abstract boolean containsKey(String key);

  /** Return the config value associated with <code>key</code>.
   * @return the string, or null if the key is not present
   * or its value is null.
   */
  public abstract String get(String key);

  /** Set the config value associated with <code>key</code>.
   * @param key the config key
   * @param val the new value
   */
  public abstract void put(String key, String val);

  /** Seal the configuration so that no changes can be made */
  public abstract void seal();

  /** Returns a Configuration instance containing all the keys at or
   * below <code>key</code>
   */
  public abstract Configuration getConfigTree(String key);

  /** Returns the set of keys in the configuration.
   */
  public abstract Set keySet();

  /** Returns an <code>Iterator</code> over all the keys in the configuration.
   */
  public abstract Iterator keyIterator();

  /** Returns an <code>Iterator</code> over all the top level
     keys in the configuration.
   */
  public abstract Iterator nodeIterator();

  /** Returns an <code>Iterator</code> over the top-level keys in the
   * configuration subtree below <code>key</code>
   */
  public abstract Iterator nodeIterator(String key);

  // static convenience methods

  /** Static convenience method to get param from current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static String getParam(String key) {
    return currentConfig.get(key);
  }

  /** Static convenience method to get param from current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static String getParam(String key, String dfault) {
    return currentConfig.get(key, dfault);
  }

  /** Static convenience method to get param from current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static boolean getBooleanParam(String key) throws InvalidParam {
    return currentConfig.getBoolean(key);
  }

  /** Static convenience method to get param from current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static boolean getBooleanParam(String key, boolean dfault) {
    return currentConfig.getBoolean(key, dfault);
  }

  /** Static convenience method to get param from current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static int getIntParam(String key) throws InvalidParam {
    return currentConfig.getInt(key);
  }

  /** Static convenience method to get param from current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static int getIntParam(String key, int dfault) {
    return currentConfig.getInt(key, dfault);
  }

  /** Static convenience method to get param from current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static long getLongParam(String key) throws InvalidParam {
    return currentConfig.getLong(key);
  }

  /** Static convenience method to get param from current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static long getLongParam(String key, long dfault) {
    return currentConfig.getLong(key, dfault);
  }

  /** Static convenience method to get param from current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static long getTimeIntervalParam(String key) throws InvalidParam {
    return currentConfig.getTimeInterval(key);
  }

  /** Static convenience method to get param from current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static long getTimeIntervalParam(String key, long dfault) {
    return currentConfig.getTimeInterval(key, dfault);
  }

  /** Static convenience method to get a <code>Configuration</code>
   * subtree from the current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static Configuration paramConfigTree(String key) {
    return currentConfig.getConfigTree(key);
  }

  /** Static convenience method to get key iterator from the
   * current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static Iterator paramKeyIterator() {
    return currentConfig.keyIterator();
  }

  /** Static convenience method to get a node iterator from the
   * current configuration.
   * Don't accidentally use this on a <code>Configuration</code> instance.
   */
  public static Iterator paramNodeIterator(String key) {
    return currentConfig.nodeIterator(key);
  }

  public static void startHandler(List urls) {
    setConfigUrls(urls);
    startHandler();
  }

  public static void startHandler() {
    if (handlerThread != null) {
      log.warning("Handler already running; stopping old one first");
      stopHandler();
    } else {
      log.info("Starting handler");
    }
    handlerThread = new HandlerThread("ConfigHandler");
    handlerThread.start();
  }

  public static void stopHandler() {
    if (handlerThread != null) {
      log.info("Stopping handler");
      handlerThread.stopHandler();
      handlerThread = null;
    } else {
//       log.warning("Attempt to stop handler when it isn't running");
    }
  }

  // Handler thread, periodicially reloads config

  private static class HandlerThread extends Thread {
    private long lastReload = 0;
    private boolean goOn = false;
    private Deadline nextReload;

    private HandlerThread(String name) {
      super(name);
    }

    public void run() {
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
      goOn = true;

      // repeat every 10ish minutes until first successful load, then
      // according to org.lockss.parameterReloadInterval, or 30 minutes.
      while (goOn) {
	if (updateConfig()) {
	  // true iff loaded config has changed
	  if (!goOn) {
	    break;
	  }
	  lastReload = TimeBase.nowMs();
	  //  	stopAndOrStartThings(true);
	  reloadInterval = getTimeIntervalParam(PARAM_RELOAD_INTERVAL,
						DEFAULT_RELOAD_INTERVAL);

	}
	long reloadRange = reloadInterval/4;
	nextReload = Deadline.inRandomRange(reloadInterval - reloadRange,
					    reloadInterval + reloadRange);
	log.debug2(nextReload.toString());
	if (goOn) {
	  try {
	    nextReload.sleep();
	  } catch (InterruptedException e) {
	    // just wakeup and check for exit
	  }
	}
      }
    }

    private void stopHandler() {
      goOn = false;
      this.interrupt();
    }

    void forceReload() {
      if (nextReload != null) {
	nextReload.expire();
      }
    }
  }

  /**
   * The <code>Configuration.Callback</code> interface defines the
   * callback registered by clients of <code>Configuration</code>
   * who want to know when the configuration has changed.
   */
  public interface Callback {
    /**
     * Callback used to inform clients that something in the configuration
     * has changed.  It is called after the new config is installed as
     * current, as well as upon registration (if there is a current
     * configuration at the time).  It is thus safe to rely solely on a
     * configuration callback to receive configuration information.
     * @param newConfig  the new (just installed) <code>Configuration</code>.
     * @param oldConfig  the previous <code>Configuration</code>, or null
     *                   if there was no previous config.
     * @param changedKeys  the set of keys whose value has changed.
     * @see Configuration#registerConfigurationCallback */
    public void configurationChanged(Configuration newConfig,
				     Configuration oldConfig,
				     Set changedKeys);
  }

  /** Exception thrown for errors in accessors. */
  public class InvalidParam extends Exception {
    public InvalidParam(String message) {
      super(message);
    }
  }
}
