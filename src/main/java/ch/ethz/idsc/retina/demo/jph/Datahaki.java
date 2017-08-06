// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

enum Datahaki {
  LOG_01(new File("/tmp", "DAVIS240C-2017-08-03T16-55-01+0200-02460045-0.aedat")), //
  LOG_02(new File("/tmp", "DAVIS240C-2017-08-03T18-16-55+0200-02460045-0.aedat")), //
  LOG_03(new File("/tmp", "DAVIS240C-2017-08-04T10-13-29+0200-02460045-0.aedat")), //
  ;
  public final File file;

  private Datahaki(File file) {
    this.file = file;
  }
}
