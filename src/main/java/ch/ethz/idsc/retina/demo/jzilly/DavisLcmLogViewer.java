// code by jph
package ch.ethz.idsc.retina.demo.jzilly;

import java.io.IOException;

import ch.ethz.idsc.retina.demo.DavisSerial;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmViewer;
import ch.ethz.idsc.tensor.RationalScalar;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum DavisLcmLogViewer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = "/Users/julianzilly/Desktop/Projects/logged_data/lcmlog-2017-08-25.00";
    cfg.speed = RationalScalar.of(1, 4);
    LogPlayer.create(cfg);
    DavisLcmViewer.createStandlone("jzilly", 25_000);
  }
}
