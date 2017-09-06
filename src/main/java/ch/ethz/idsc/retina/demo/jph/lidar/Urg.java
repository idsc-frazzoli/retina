// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import java.io.File;

enum Urg {
  LCMLOG01(new File("/media/datahaki/media/ethz/lcmlog", "lcmlog-2017-09-03.00.urg_bin")), //
  ;
  public final File file;

  private Urg(File file) {
    this.file = file;
  }
}
