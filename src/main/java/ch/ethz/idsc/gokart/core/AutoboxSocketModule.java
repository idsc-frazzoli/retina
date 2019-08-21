// code by jph
package ch.ethz.idsc.gokart.core;

import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.misc.MiscSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** communication link between pc and micro-autobox.
 * 
 * The gokart cannot be operated without AutoboxSocketModule. */
public final class AutoboxSocketModule extends AbstractModule {
  @Override // from AbstractModule
  protected void first() {
    RimoSocket.INSTANCE.start();
    LinmotSocket.INSTANCE.start();
    SteerSocket.INSTANCE.start();
    MiscSocket.INSTANCE.start();
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.stop();
    LinmotSocket.INSTANCE.stop();
    SteerSocket.INSTANCE.stop();
    MiscSocket.INSTANCE.stop();
  }
}
