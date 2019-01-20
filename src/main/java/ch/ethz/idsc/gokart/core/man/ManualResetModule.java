// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.GokartActuatorCalibration;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** the module monitors the reset button of the joystick.
 * when the button is presses by the operator, the module schedules
 * the calibration procedure for the devices that are not calibrated.
 * the devices are: misc, linmot, and steer. */
public class ManualResetModule extends AbstractClockedModule {
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();

  @Override
  protected void runAlgo() {
    Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
    if (optional.isPresent()) {
      ManualControlInterface manualControlInterface = optional.get();
      if (manualControlInterface.isResetPressed())
        GokartActuatorCalibration.all();
    }
  }

  @Override
  protected void first() throws Exception {
    manualControlProvider.start();
  }

  @Override
  protected void last() {
    manualControlProvider.stop();
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(0.1, SI.SECOND);
  }
}
