// code by jph
package ch.ethz.idsc.retina.lcm.mod;

import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmServer;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** intended to run on gokart */
public class Urg04lxLcmServerModule extends AbstractModule {
  private final Urg04lxLcmServer urg04lxLcmServer = new Urg04lxLcmServer(GokartLcmChannel.URG04LX_FRONT);

  @Override
  protected void first() throws Exception {
    urg04lxLcmServer.start();
  }

  @Override
  protected void last() {
    urg04lxLcmServer.start();
  }
}
