// code by mh
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** overwrites steering and Rimo command if designated joystick button is not pushed
 * 
 * The put providers are implemented as anonymous classes */
public final class AutonomySafetyModule extends AbstractModule {
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  final AutonomySafetyRimo autonomySafetyRimo = new AutonomySafetyRimo(() -> isAutonomousPressed());
  final AutonomySafetySteer autonomySafetySteer = new AutonomySafetySteer(() -> isAutonomousPressed());

  @Override // from AbstractModule
  protected void first() throws Exception {
    manualControlProvider.start();
    SteerSocket.INSTANCE.addPutProvider(autonomySafetySteer);
    RimoSocket.INSTANCE.addPutProvider(autonomySafetyRimo);
  }

  @Override // from AbstractModule
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(autonomySafetySteer);
    RimoSocket.INSTANCE.removePutProvider(autonomySafetyRimo);
    manualControlProvider.stop();
  }

  private boolean isAutonomousPressed() {
    Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
    if (optional.isPresent()) { // is joystick button "autonomous" pressed?
      ManualControlInterface gokartJoystickInterface = optional.get();
      return gokartJoystickInterface.isAutonomousPressed();
    }
    return false;
  }
}
