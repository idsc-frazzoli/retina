// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.lidar.Urg04lxLcmServer;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

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
