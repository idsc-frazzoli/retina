// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.core.StartAndStoppable;
import ch.ethz.idsc.retina.hdl32e.Hdl32eFiringPacketConsumer;
import ch.ethz.idsc.retina.hdl32e.Hdl32eLiveFiringProvider;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Hdl32eLivePanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer = PanoramaUtils.createDisplay();
    StartAndStoppable hw = new Hdl32eLiveFiringProvider(hdl32eFiringPacketConsumer);
    hw.start();
  }
}
