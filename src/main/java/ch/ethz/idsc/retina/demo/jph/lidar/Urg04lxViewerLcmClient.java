// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxRangeProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.app.Urg04lxFrame;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmHandler;

enum Urg04lxViewerLcmClient {
  ;
  public static void main(String[] args) {
    Urg04lxFrame urg04lxFrame = new Urg04lxFrame();
    Urg04lxLcmHandler simpleUrg04lxLcmClient = new Urg04lxLcmHandler("front");
    simpleUrg04lxLcmClient.lidarAngularFiringCollector.addListener(urg04lxFrame.urg04lxRender);
    {
      Urg04lxRangeProvider urg04lxRangeProvider = new Urg04lxRangeProvider();
      urg04lxRangeProvider.addListener(urg04lxFrame.urg04lxRender);
      simpleUrg04lxLcmClient.urg04lxDecoder().addRayListener(urg04lxRangeProvider);
    }
  }
}
