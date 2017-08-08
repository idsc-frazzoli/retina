// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringPacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringProvider;
import ch.ethz.idsc.retina.dev.hdl32e.LiveHdl32eFiringProvider;

enum LiveHdl32eImage {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer = PanoramaUtils.createDisplay();
    Hdl32eFiringProvider hdl32eFiringProvider = new LiveHdl32eFiringProvider(hdl32eFiringPacketConsumer);
    hdl32eFiringProvider.start();
  }
}
