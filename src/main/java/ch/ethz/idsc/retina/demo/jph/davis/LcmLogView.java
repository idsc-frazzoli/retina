// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import java.io.IOException;

import ch.ethz.idsc.retina.demo.DavisSerial;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmViewer;
import ch.ethz.idsc.tensor.RationalScalar;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum LcmLogView {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = "/media/datahaki/media/ethz/lcmlog/lcmlog-2017-08-23.davis";
    cfg.speed = RationalScalar.of(1, 4);
    LogPlayer.create(cfg);
    DavisLcmViewer.createStandlone(DavisSerial.FX2_02460045.name(), 25_000);
  }
}
