// code by jph
package ch.ethz.idsc.gokart.offline.cache;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import ch.ethz.idsc.retina.util.io.ContentType;
import ch.ethz.idsc.retina.util.io.URLFetch;

public enum CachedLog {
  /** glc trajectory planning with changing obstacles
   * 425 MB */
  _20190401T115537_00("2zWaR5TsAel8A5I"), //
  /** glc trajectory planning unsuccessful with contact
   * 101 MB */
  _20190401T115537_01("13yPd467TNOIMqb"), //
  /** slow manual driving
   * 68 MB */
  _20190401T115537_02("kJXHxiOEqM9ZTud"), //
  /** mpc with contact
   * 38 MB */
  _20190404T143912_20("XK289WtD35MWLJm"), //
  /** fast manual driving
   * 70 MB */
  _20190404T143912_21("HYRGdhMDw64hAQL"), //
  /** fast mpc
   * 101 MB */
  _20190404T143912_22("LAUvA2ZYpq3o3Ep"), //
  /** fast mpc
   * 91 MB */
  _20190404T143912_23("eXjErZhuiFZqsAz"), //
  /** slow manual driving
   * 19 MB */
  _20190404T143912_24("j0pTiysXiboDBtl"), //
  /** fast mpc
   * 37 MB */
  _20190404T143912_25("rvVNWrDvGbXy3GC"), //
  /** fast manual driving
   * 75 MB */
  _20190404T143912_26("zLvr6HdaLhjTJwk"), //
  /** fast manual driving by mhug
   * 116 MB */
  _20190701T170957_04("C5dWajPxVPpwu1A"), //
  /** one lap of track recon
   * 34 MB */
  _20190701T174152_00("dcGUfMSuo1kcnbQ"), //
  /** fast mpc
   * 46 MB */
  _20190701T175650_01("egM3ECnilFzt6q6"), //
  //
  ;
  private final String id;

  private CachedLog(String id) {
    this.id = id;
  }

  public String title() {
    return name().substring(1);
  }

  public URL url() {
    try {
      return new URL("https://polybox.ethz.ch/index.php/s/" + id + "/download");
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }

  public File file() throws IOException {
    File directory = new File("resources/cache/lcm", name().substring(1, 1 + 8));
    directory.mkdirs();
    File file = new File(directory, title() + ".lcm");
    if (!file.isFile())
      try (URLFetch urlFetch = new URLFetch(url())) {
        ContentType.APPLICATION_OCTETSTREAM.require(urlFetch.contentType());
        urlFetch.downloadIfMissing(file);
      }
    return file;
  }
}
