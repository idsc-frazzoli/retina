// code by jph
package ch.ethz.idsc.demo.gz;

import java.io.File;

import ch.ethz.idsc.retina.lcm.davis.DavisLcmLogUzhConvert;

enum RunDavisLcmLogUzhConvert {
  ;
  public static void main(String[] args) {
    File file = new File("/home/gio/Downloads/logs/log.lcm");
    File target = new File("/home/gio/Downloads/logs");
    DavisLcmLogUzhConvert.of(file, target);
  }
}
