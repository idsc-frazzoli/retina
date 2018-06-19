// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

enum Aedat20 {
  LOG_01(new File("does not exist", "file.aedat")), //
  ;
  public final File file;

  private Aedat20(File file) {
    this.file = file;
  }
}
