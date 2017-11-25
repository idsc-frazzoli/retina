// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class LinmotJoystickModule extends AbstractModule implements LinmotPutProvider {
  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    LinmotSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    Optional<GokartJoystickInterface> optional = null; // FIXME
    if (optional.isPresent()) {
      GokartJoystickInterface joystick = optional.get();
      return Optional.of(LinmotPutHelper.operationToRelativePosition(joystick.getBreakStrength()));
    }
    return Optional.empty();
  }
}
