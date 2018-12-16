// code by jph
package ch.ethz.idsc.gokart.core;

import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.misc.MiscSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** communication link between pc and micro-autobox.
 * operation of the gokart without AutoboxSocketModule is not possible. */
public class AutoboxSocketModule extends AbstractModule {
  @Override
  protected void first() throws Exception {
    RimoSocket.INSTANCE.start();
    LinmotSocket.INSTANCE.start();
    SteerSocket.INSTANCE.start();
    MiscSocket.INSTANCE.start();
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.stop();
    LinmotSocket.INSTANCE.stop();
    SteerSocket.INSTANCE.stop();
    MiscSocket.INSTANCE.stop();
  }
}
