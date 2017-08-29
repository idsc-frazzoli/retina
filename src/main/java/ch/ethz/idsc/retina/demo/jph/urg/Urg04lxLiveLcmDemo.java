// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxLiveProvider;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmServer;

/** for the demo, the sensor has to be connected to the pc */
enum Urg04lxLiveLcmDemo {
  ;
  public static void main(String[] args) throws Exception {
    Urg04lxLcmServer urg04lxLcmServer = new Urg04lxLcmServer("front");
    Urg04lxLiveProvider.INSTANCE.addListener(urg04lxLcmServer);
    Urg04lxLiveProvider.INSTANCE.start();
  }
}
