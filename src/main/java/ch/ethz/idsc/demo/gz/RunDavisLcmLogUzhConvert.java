// code by jph
package ch.ethz.idsc.demo.gz;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.davis.DavisLcmLogUzhConvert;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum RunDavisLcmLogUzhConvert {
  ;
  public static void main(String[] args) {
    File file = HomeDirectory.file("Downloads/logs/log.lcm");
    File target = HomeDirectory.file("Downloads/logs");
    DavisLcmLogUzhConvert.of(file, target);
  }
}
