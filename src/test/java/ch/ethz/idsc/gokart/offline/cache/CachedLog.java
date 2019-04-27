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
    File file = new File("resources/cache/lcm", title() + ".lcm");
    if (!file.isFile())
      new URLFetch(url(), ContentType.APPLICATION_OCTETSTREAM).to(file);
    return file;
  }
}
