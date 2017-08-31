// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import java.io.IOException;

import ch.ethz.idsc.tensor.RationalScalar;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum LcmLogViewerDemo {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = Hdl32eLcm.OFFICE1;
    cfg.speed = RationalScalar.of(1, 8);
    LogPlayer.create(cfg);
    // ---
    // Hdl32eLcmViewer.create("center");
  }
}
