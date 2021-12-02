package org.lockss.plugin.archiveit;

import org.lockss.daemon.ConfigParamDescr;
import org.lockss.daemon.OpenUrlResolver;
import org.lockss.daemon.PluginException;
import org.lockss.plugin.*;
import org.lockss.util.ListUtil;
import org.lockss.util.Logger;
import org.lockss.util.TypedEntryMap;
import org.lockss.util.UrlUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ArchiveItApiFeatureUrlHelperFactory implements FeatureUrlHelperFactory {
  private static final Logger log = Logger.getLogger(ArchiveItApiFeatureUrlHelperFactory.class);


  @Override
  public FeatureUrlHelper createFeatureUrlHelper(Plugin plug) {
    return new ArchiveItFeatureUrlHelper();
  }

  private static class ArchiveItFeatureUrlHelper extends BaseFeatureUrlHelper {

    @Override
    public Collection<String> getAccessUrls(ArchivalUnit au)
        throws PluginException, IOException {

      if (au == null) {
        return null;
      }
      // return the synthetic url
      return (ListUtil.list(getSyntheticUrl(au)));
    }

    @Override
    public List<String> getFeatureUrls(ArchivalUnit au,
                                       OpenUrlResolver.OpenUrlInfo.ResolvedTo itemType,
                                       TypedEntryMap paramMap)
        throws PluginException, IOException {

      // This can actually get called with null AU from OpenUrlResolver
      if (au == null) {
        return null;
      }
      if (itemType == OpenUrlResolver.OpenUrlInfo.ResolvedTo.VOLUME) {
        return (ListUtil.list(getSyntheticUrl(au)));
      } else {
        return null;
      }
    }
    public String getSyntheticUrl(ArchivalUnit au) {
      // synthetic url, if you want to update the pattern you must update it in all of these places
      // 1. ArchiveItApiPlugin - crawl_rules
      // 2. ArchiveItApiCrawlSeed.populateUrlList()
      // 3. ArchiveItApiFeatureUrlHelperFactory.getSyntheticUrl()
      String sytheticUrl =
          au.getConfiguration().get(ConfigParamDescr.BASE_URL.getKey()) +
          "organization=" + UrlUtil.encodeUrl(au.getConfiguration().get("organization")) +
          "&collection=" + UrlUtil.encodeUrl(au.getConfiguration().get(ConfigParamDescr.COLLECTION.getKey()));
      return sytheticUrl;
    }
  }
}
