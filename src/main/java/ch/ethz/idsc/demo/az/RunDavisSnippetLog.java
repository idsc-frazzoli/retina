// code by jph
package ch.ethz.idsc.demo.az;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.davis.DavisSnippetLog;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum RunDavisSnippetLog {
  ;
  public static void main(String[] args) {
    int period_ms = 1200;
    File lcmDir = HomeDirectory.file("Datasets", "ourCityscape_lcm");
    File uzhDir = HomeDirectory.file("Datasets", "ourCityscape_uzh");
    new DavisSnippetLog(period_ms, lcmDir, uzhDir);
  }
}
