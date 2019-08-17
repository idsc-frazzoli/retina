// code by jph
package ch.ethz.idsc.gokart.offline.cache;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import ch.ethz.idsc.retina.util.io.ContentType;
import ch.ethz.idsc.retina.util.io.URLFetch;

public enum CachedLog {
  /** 425 MB */
  _20190401T115537_00("2zWaR5TsAel8A5I"), //
  /** 101 MB */
  _20190401T115537_01("13yPd467TNOIMqb"), //
  /** 68 MB */
  _20190401T115537_02("kJXHxiOEqM9ZTud"), //
  /** 38 MB */
  _20190404T143912_20("XK289WtD35MWLJm"), //
  /** 70 MB */
  _20190404T143912_21("HYRGdhMDw64hAQL"), //
  /** 101 MB */
  _20190404T143912_22("LAUvA2ZYpq3o3Ep"), //
  /** 91 MB */
  _20190404T143912_23("eXjErZhuiFZqsAz"), //
  /** 19 MB */
  _20190404T143912_24("j0pTiysXiboDBtl"), //
  /** 37 MB */
  _20190404T143912_25("rvVNWrDvGbXy3GC"), //
  /** 75 MB */
  _20190404T143912_26("zLvr6HdaLhjTJwk"), //
  /** 34 MB */
  _20190701T174152_00("dcGUfMSuo1kcnbQ"), //
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
    File root = new File("resources/cache/lcm", name().substring(1, 1 + 8));
    root.mkdirs();
    File file = new File(root, title() + ".lcm");
    if (!file.isFile())
      new URLFetch(url(), ContentType.APPLICATION_OCTETSTREAM).to(file);
    return file;
  }
}
