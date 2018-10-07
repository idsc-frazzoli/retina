// code by mh
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** overwrites steering and Rimo command if designated joystick button is not pushed
 * 
 * The put providers are implemented as anonymous classes */
public final class AutonomySafetyModule extends AbstractModule {
  private final JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();
  // rimo
  private final PutProvider<RimoPutEvent> putProviderRimo = new PutProvider<RimoPutEvent>() {
    @Override
    public Optional<RimoPutEvent> putEvent() {
      return isAutonomousPressed() //
          ? Optional.empty()
          : StaticHelper.OPTIONAL_RIMO_PASSIVE;
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.SAFETY;
    }
  };
  // steering
  private final PutProvider<SteerPutEvent> putProviderSteer = new PutProvider<SteerPutEvent>() {
    @Override
    public Optional<SteerPutEvent> putEvent() {
      return isAutonomousPressed() //
          ? Optional.empty()
          : Optional.of(SteerPutEvent.PASSIVE_MOT_TRQ_0);
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.SAFETY;
    }
  };

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmProvider.startSubscriptions();
    SteerSocket.INSTANCE.addPutProvider(putProviderSteer);
    RimoSocket.INSTANCE.addPutProvider(putProviderRimo);
  }

  @Override // from AbstractModule
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(putProviderSteer);
    RimoSocket.INSTANCE.removePutProvider(putProviderRimo);
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
