// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import java.io.File;

enum Urg {
  LCMLOG01(new File("/media/datahaki/media/ethz/lcmlog", "lcmlog-2017-09-03.00.urg_bin")), //
  LCMLOG02(new File("/media/datahaki/media/ethz/lcmlog", "lcmlog-2017-08-31.urg04lx_mark8")), //
  ;
  public final File file;

  private Urg(File file) {
    this.file = file;
  }
}
