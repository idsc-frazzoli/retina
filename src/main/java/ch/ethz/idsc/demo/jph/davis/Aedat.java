// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

enum Aedat {
  LOG_04(new File("/media/datahaki/media/ethz/davis240c/aedat", //
      "DAVIS240C-2017-08-17T14-01-14+0200-02460045-0.aedat")), //
  ;
  public final File file;

  private Aedat(File file) {
    this.file = file;
  }
}
