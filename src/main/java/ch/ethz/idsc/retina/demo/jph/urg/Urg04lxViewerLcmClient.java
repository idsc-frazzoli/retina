// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import ch.ethz.idsc.retina.dev.lidar.urg04lx.app.Urg04lxFrame;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmClient;

enum Urg04lxViewerLcmClient {
  ;
  public static void launch() {
    Urg04lxLcmClient urg04lxLcmClient = new Urg04lxLcmClient("front");
    Urg04lxFrame urg04lxFrame = new Urg04lxFrame();
    urg04lxLcmClient.addListener(urg04lxFrame);
    urg04lxLcmClient.startSubscriptions();
  }

  public static void main(String[] args) {
    launch();
  }
}
