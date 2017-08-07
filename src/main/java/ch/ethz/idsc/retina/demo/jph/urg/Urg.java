// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import java.io.File;

enum Urg {
  LOG01(new File("/media/datahaki/media/ethz/urg04lx/", "urg20170727T132829.txt")), //
  LOG02(new File("/media/datahaki/media/ethz/urg04lx/", "urg20170727T133009.txt")), //
  LOG03(new File("/media/datahaki/media/ethz/urg04lx/", "urg20170727T133133.txt")), //
  LOG04(new File("/media/datahaki/media/ethz/urg04lx/", "urg20170727T135745.txt")), //
  ;
  public final File file;

  private Urg(File file) {
    this.file = file;
  }
}
