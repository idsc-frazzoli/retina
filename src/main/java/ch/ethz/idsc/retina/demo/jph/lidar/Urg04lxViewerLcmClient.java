// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxDecoder;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxRangeProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.app.Urg04lxFrame;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmClient;

enum Urg04lxViewerLcmClient {
  ;
  public static void main(String[] args) {
    Urg04lxDecoder urg04lxDecoder = new Urg04lxDecoder();
    Urg04lxLcmClient urg04lxLcmClient = new Urg04lxLcmClient(urg04lxDecoder, "front");
    Urg04lxFrame urg04lxFrame = new Urg04lxFrame();
    Urg04lxRangeProvider urg04lxEventProvider = new Urg04lxRangeProvider();
    urg04lxDecoder.addRayListener(urg04lxEventProvider);
    urg04lxEventProvider.addListener(urg04lxFrame);
    urg04lxLcmClient.startSubscriptions();
  }
}
