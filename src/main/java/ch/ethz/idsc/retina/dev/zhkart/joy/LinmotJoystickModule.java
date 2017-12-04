// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** conversion of joystick event to brake command */
public class LinmotJoystickModule extends AbstractModule implements LinmotPutProvider {
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmClient.startSubscriptions();
    LinmotSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    LinmotSocket.INSTANCE.removePutProvider(this);
    joystickLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override // from LinmotPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override // from LinmotPutProvider
  public Optional<LinmotPutEvent> putEvent() {
    Optional<JoystickEvent> optional = joystickLcmClient.getJoystick();
    if (optional.isPresent()) {
      GokartJoystickInterface joystick = (GokartJoystickInterface) optional.get();
      return Optional.of(LinmotPutHelper.operationToRelativePosition(joystick.getBreakStrength()));
    }
    return Optional.empty();
  }
}
