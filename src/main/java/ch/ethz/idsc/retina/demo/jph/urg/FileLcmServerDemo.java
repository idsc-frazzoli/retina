// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxFileProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxRealtimeListener;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmServer;

enum FileLcmServerDemo {
  ;
  public static void main(String[] args) throws Exception {
    Urg04lxProvider urg04lxProvider = new Urg04lxFileProvider(Urg.LOG05.file);
    urg04lxProvider.addListener(new Urg04lxRealtimeListener(1.0));
    // ---
    Urg04lxLcmServer urg04lxLcmServer = new Urg04lxLcmServer("front");
    urg04lxProvider.addListener(urg04lxLcmServer);
    urg04lxProvider.start();
    urg04lxProvider.stop();
  }
}
