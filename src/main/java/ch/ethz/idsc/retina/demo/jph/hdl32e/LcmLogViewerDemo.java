// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import java.io.IOException;

import ch.ethz.idsc.retina.lcm.lidar.Hdl32eLcmViewer;
import ch.ethz.idsc.tensor.RationalScalar;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum LcmLogViewerDemo {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = "/media/datahaki/media/ethz/lcmlog/lcmlog-2017-08-24.hdl32e";
    cfg.speed = RationalScalar.of(1, 8);
    LogPlayer.create(cfg);
    // ---
    Hdl32eLcmViewer.create("center");
  }
}
