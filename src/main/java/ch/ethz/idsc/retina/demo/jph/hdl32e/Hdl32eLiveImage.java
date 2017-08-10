// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.hdl32e.Hdl32eFiringPacketConsumer;
import ch.ethz.idsc.retina.hdl32e.Hdl32eLiveFiringProvider;
import ch.ethz.idsc.retina.hdl32e.Hdl32eLiveProvider;

enum Hdl32eLiveImage {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer = PanoramaUtils.createDisplay();
    Hdl32eLiveProvider hdl32eFiringProvider = new Hdl32eLiveFiringProvider(hdl32eFiringPacketConsumer);
    hdl32eFiringProvider.start();
  }
}
