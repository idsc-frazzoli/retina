// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringProvider;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.dev.hdl32e.LiveHdl32eFiringProvider;

enum LiveHdl32eImage {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32eFiringProvider hdl32eFiringProvider = //
        new LiveHdl32eFiringProvider(new Hdl32ePanoramaCollector(hdl32ePanoramaFrame));
    hdl32eFiringProvider.start();
  }
}
