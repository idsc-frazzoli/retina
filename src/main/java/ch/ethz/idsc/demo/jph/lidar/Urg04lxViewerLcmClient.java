// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.lidar.Urg04lxLcmHandler;
import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxRangeProvider;
import ch.ethz.idsc.retina.lidar.urg04lx.app.Urg04lxFrame;

enum Urg04lxViewerLcmClient {
  ;
  public static void main(String[] args) {
    Urg04lxFrame urg04lxFrame = new Urg04lxFrame();
    Urg04lxLcmHandler urg04lxLcmHandler = new Urg04lxLcmHandler(GokartLcmChannel.URG04LX_FRONT);
    urg04lxLcmHandler.lidarAngularFiringCollector.addListener(urg04lxFrame.urg04lxRender);
    {
      Urg04lxRangeProvider urg04lxRangeProvider = new Urg04lxRangeProvider();
      urg04lxRangeProvider.addListener(urg04lxFrame.urg04lxRender);
      urg04lxLcmHandler.urg04lxDecoder().addRayListener(urg04lxRangeProvider);
    }
    urg04lxLcmHandler.startSubscriptions();
  }
}
