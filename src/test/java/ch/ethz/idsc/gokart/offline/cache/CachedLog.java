// code by jph
package ch.ethz.idsc.gokart.offline.cache;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.util.io.ContentType;
import ch.ethz.idsc.retina.util.io.URLFetch;

public enum CachedLog {
  _20190401T115537_00("2zWaR5TsAel8A5I"), //
  _20190401T115537_01("13yPd467TNOIMqb"), //
  _20190401T115537_02("kJXHxiOEqM9ZTud"), //
  _20190404T143912_20("XK289WtD35MWLJm"), //
  _20190404T143912_21("HYRGdhMDw64hAQL"), //
  _20190404T143912_22("LAUvA2ZYpq3o3Ep"), //
  _20190404T143912_23("eXjErZhuiFZqsAz"), //
  _20190404T143912_24("j0pTiysXiboDBtl"), //
  _20190404T143912_25("rvVNWrDvGbXy3GC"), //
  _20190404T143912_26("zLvr6HdaLhjTJwk"), //
  ;
  private final String id;

  private CachedLog(String id) {
    this.id = id;
  }

  public String title() {
    return name().substring(1);
  }

  public String url() {
    return "https://polybox.ethz.ch/index.php/s/" + id + "/download";
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
