// code by mh
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.joy.ManualConfig;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** overwrites steering and Rimo command if designated joystick button is not pushed
 * 
 * The put providers are implemented as anonymous classes */
public final class AutonomySafetyModule extends AbstractModule {
  private final ManualControlProvider joystickLcmProvider = ManualConfig.GLOBAL.createProvider();
  final AutonomySafetyRimo autonomySafetyRimo = new AutonomySafetyRimo(() -> isAutonomousPressed());
  final AutonomySafetySteer autonomySafetySteer = new AutonomySafetySteer(() -> isAutonomousPressed());

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmProvider.start();
    SteerSocket.INSTANCE.addPutProvider(autonomySafetySteer);
    RimoSocket.INSTANCE.addPutProvider(autonomySafetyRimo);
  }

  @Override // from AbstractModule
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(autonomySafetySteer);
    RimoSocket.INSTANCE.removePutProvider(autonomySafetyRimo);
    joystickLcmProvider.stop();
  }

  private boolean isAutonomousPressed() {
    Optional<GokartJoystickInterface> joystick = joystickLcmProvider.getJoystick();
    if (joystick.isPresent()) { // is joystick button "autonomous" pressed?
      GokartJoystickInterface gokartJoystickInterface = joystick.get();
      return gokartJoystickInterface.isAutonomousPressed();
    }
    return false;
  }
}
