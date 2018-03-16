// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

enum Aedat {
  LOG_04(new File("/media/datahaki/media/ethz/insightness/aedat", //
      "sees_control_recording_2018_03_16-15_36_46.aedat")), //
  ;
  public final File file;

  private Aedat(File file) {
    this.file = file;
  }
}
