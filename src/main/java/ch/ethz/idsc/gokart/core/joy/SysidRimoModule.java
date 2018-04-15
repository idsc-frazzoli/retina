// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.Scalar;

/** module generates ... */
public class SysidRimoModule extends AbstractModule implements PutProvider<RimoPutEvent> {
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);

  @Override
  protected void first() throws Exception {
    joystickLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    joystickLcmClient.stopSubscriptions();
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    Optional<JoystickEvent> joystick = joystickLcmClient.getJoystick();
    if (joystick.isPresent()) {
      GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
      if (gokartJoystickInterface.isAutonomousPressed()) {
        Scalar aheadAverage = gokartJoystickInterface.getAheadAverage();
      }
    }
    return Optional.empty();
  }
}
