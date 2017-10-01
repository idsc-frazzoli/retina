// code by jph
package ch.ethz.idsc.retina.lcm.mod;

import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.retina.lcm.autobox.MiscLcmServer;
import ch.ethz.idsc.retina.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class AutoboxLcmServerModule extends AbstractModule {
  @Override
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addAll(RimoLcmServer.INSTANCE);
    LinmotSocket.INSTANCE.addAll(LinmotLcmServer.INSTANCE);
    SteerSocket.INSTANCE.addAll(SteerLcmServer.INSTANCE);
    MiscSocket.INSTANCE.addAll(MiscLcmServer.INSTANCE);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removeAll(RimoLcmServer.INSTANCE);
    LinmotSocket.INSTANCE.removeAll(LinmotLcmServer.INSTANCE);
    SteerSocket.INSTANCE.removeAll(SteerLcmServer.INSTANCE);
    MiscSocket.INSTANCE.removeAll(MiscLcmServer.INSTANCE);
  }
}
