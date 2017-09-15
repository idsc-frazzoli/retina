// code by jph
package ch.ethz.idsc.retina.lcm.mod;

import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmServer;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class Urg04lxLcmServerModule extends AbstractModule {
  @Override
  protected void first() throws Exception {
    Urg04lxLcmServer.INSTANCE.start();
  }

  @Override
  protected void last() {
    Urg04lxLcmServer.INSTANCE.start();
  }
}
