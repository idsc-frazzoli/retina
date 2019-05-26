// code by mh
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** overwrites steering and Rimo command if designated
 * joystick/autonomous mode button is not pressed
 * 
 * The put providers are implemented as anonymous classes */
@Deprecated
final class AutonomySafetyModule extends AbstractModule {
  final AutonomySafetyRimo autonomySafetyRimo = new AutonomySafetyRimo(this::isAutonomousPressed);
  final AutonomySafetySteer autonomySafetySteer = new AutonomySafetySteer(this::isAutonomousPressed);
  private final ManualControlProvider manualControlProvider;

  public AutonomySafetyModule() {
    this(ManualConfig.GLOBAL.getProvider());
  }

  public AutonomySafetyModule(ManualControlProvider manualControlProvider) {
    this.manualControlProvider = Objects.requireNonNull(manualControlProvider);
  }

  @Override // from AbstractModule
  protected void first() {
    SteerSocket.INSTANCE.addPutProvider(autonomySafetySteer);
    RimoSocket.INSTANCE.addPutProvider(autonomySafetyRimo);
  }

  @Override // from AbstractModule
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(autonomySafetySteer);
    RimoSocket.INSTANCE.removePutProvider(autonomySafetyRimo);
  }

  private boolean isAutonomousPressed() {
    Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
    if (optional.isPresent()) {
      ManualControlInterface manualControlInterface = optional.get();
      return manualControlInterface.isAutonomousPressed(); // is button "autonomous" pressed?
    }
    return false;
  }
}
