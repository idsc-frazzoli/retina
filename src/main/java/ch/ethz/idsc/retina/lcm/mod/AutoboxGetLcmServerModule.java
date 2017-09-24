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

public class AutoboxGetLcmServerModule extends AbstractModule {
  @Override
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addListener(RimoLcmServer.INSTANCE);
    LinmotSocket.INSTANCE.addListener(LinmotLcmServer.INSTANCE);
    SteerSocket.INSTANCE.addListener(SteerLcmServer.INSTANCE);
    MiscSocket.INSTANCE.addListener(MiscLcmServer.INSTANCE);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removeListener(RimoLcmServer.INSTANCE);
    LinmotSocket.INSTANCE.removeListener(LinmotLcmServer.INSTANCE);
    SteerSocket.INSTANCE.removeListener(SteerLcmServer.INSTANCE);
    MiscSocket.INSTANCE.removeListener(MiscLcmServer.INSTANCE);
  }
}
