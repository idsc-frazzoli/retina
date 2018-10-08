// code by mh
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** overwrites steering and Rimo command if designated joystick button is not pushed
 * 
 * The put providers are implemented as anonymous classes */
public final class AutonomySafetyModule extends AbstractModule {
  private final JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();
  // rimo
  private final AutonomySafetyRimo autonomySafetyRimo = new AutonomySafetyRimo(() -> isAutonomousPressed());
  private final AutonomySafetySteer autonomySafetySteer = new AutonomySafetySteer(() -> isAutonomousPressed());

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmProvider.startSubscriptions();
    SteerSocket.INSTANCE.addPutProvider(autonomySafetySteer);
    RimoSocket.INSTANCE.addPutProvider(autonomySafetyRimo);
  }

  @Override // from AbstractModule
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(autonomySafetySteer);
    RimoSocket.INSTANCE.removePutProvider(autonomySafetyRimo);
    joystickLcmProvider.stopSubscriptions();
  }

  private boolean isAutonomousPressed() {
    Optional<JoystickEvent> joystick = joystickLcmProvider.getJoystick();
    if (joystick.isPresent()) { // is joystick button "autonomous" pressed?
      GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
      return gokartJoystickInterface.isAutonomousPressed();
    }
    return false;
  }
}
