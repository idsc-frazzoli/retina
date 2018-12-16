// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.misc.MiscSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.MiscLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** module subscribes and unsubscribes to all micro-autobox channels
 * and publishes the traffic via lcm.
 * 
 * One application is to log the messages received from and
 * the commands sent to the micro-autobox. */
public class AutoboxLcmServerModule extends AbstractModule {
  @Override // from AbstractModule
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addGetListener(RimoLcmServer.INSTANCE);
    RimoSocket.INSTANCE.addPutListener(RimoLcmServer.INSTANCE);
    // ---
    LinmotSocket.INSTANCE.addGetListener(LinmotLcmServer.INSTANCE);
    LinmotSocket.INSTANCE.addPutListener(LinmotLcmServer.INSTANCE);
    // ---
    SteerSocket.INSTANCE.addGetListener(SteerLcmServer.INSTANCE);
    SteerSocket.INSTANCE.addPutListener(SteerLcmServer.INSTANCE);
    // ---
    MiscSocket.INSTANCE.addGetListener(MiscLcmServer.INSTANCE);
    MiscSocket.INSTANCE.addPutListener(MiscLcmServer.INSTANCE);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removeGetListener(RimoLcmServer.INSTANCE);
    RimoSocket.INSTANCE.removePutListener(RimoLcmServer.INSTANCE);
    // ---
    LinmotSocket.INSTANCE.removeGetListener(LinmotLcmServer.INSTANCE);
    LinmotSocket.INSTANCE.removePutListener(LinmotLcmServer.INSTANCE);
    // ---
    SteerSocket.INSTANCE.removeGetListener(SteerLcmServer.INSTANCE);
    SteerSocket.INSTANCE.removePutListener(SteerLcmServer.INSTANCE);
    // ---
    MiscSocket.INSTANCE.removeGetListener(MiscLcmServer.INSTANCE);
    MiscSocket.INSTANCE.removePutListener(MiscLcmServer.INSTANCE);
  }
}
