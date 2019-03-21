// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;

/** for testing purpose only */
/* package */ class ConstantManualControlProvider implements ManualControlProvider {
  private final ManualControlInterface manualControlInterface;

  public ConstantManualControlProvider(ManualControlInterface manualControlInterface) {
    this.manualControlInterface = manualControlInterface;
  }

  @Override
  public Optional<ManualControlInterface> getManualControl() {
    return Optional.of(manualControlInterface);
  }

  @Override
  public void start() {
    // ---
  }

  @Override
  public void stop() {
    // ---
  }
}
