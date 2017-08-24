// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import java.io.IOException;

import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.lcm.lidar.Hdl32eLcmClient;
import ch.ethz.idsc.tensor.RationalScalar;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum LcmLogView {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = "/media/datahaki/media/ethz/lcmlog/lcmlog-2017-08-23.hdl32e";
    cfg.speed = RationalScalar.of(1, 8);
    LogPlayer.create(cfg);
    // ---
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    // ---
    Hdl32eLcmClient client = new Hdl32eLcmClient("center");
    client.hdl32eFiringPacketDecoder.addListener(hdl32ePanoramaCollector);
    client.subscribe();
  }
}
