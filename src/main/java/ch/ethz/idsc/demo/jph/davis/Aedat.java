// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

enum Aedat {
  LOG_01(new File("/media/datahaki/media/ethz/insightness/aedat", //
      "sees_control_recording_2018_03_16-15_36_46.aedat")), //
  LOG_02(new File("/media/datahaki/media/ethz/insightness/aedat", //
      "sees_control_recording_2018_03_16-15_40_07.aedat")), //
  LOG_03(new File("/media/datahaki/media/ethz/insightness/aedat", //
      "sees_control_recording_2018_03_16-15_42_12.aedat")), //
  LOG_04(new File("/media/datahaki/media/ethz/insightness/aedat", //
      "sees_control_recording_2018_03_16-15_44_43.aedat")), //
  ;
  public final File file;

  private Aedat(File file) {
    this.file = file;
  }
}
