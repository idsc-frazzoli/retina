// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringPacketDecoder;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanoramaFrame;

public enum PanoramaUtils {
  ;
  // ---
  static Hdl32eFiringPacketDecoder createDisplay() {
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    Hdl32eFiringPacketDecoder hdl32eFiringPacketConsumer = new Hdl32eFiringPacketDecoder();
    hdl32eFiringPacketConsumer.addListener(hdl32ePanoramaCollector);
    return hdl32eFiringPacketConsumer;
  }
}
