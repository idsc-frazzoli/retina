// code by mh
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;

//TODO: configurable
//TODO: comment approprietly
/** overwrites linmot, steering and Rimocommand if designated joystick button is
 * not pushed */
public final class AutonomyButtonSafetyModule extends AbstractModule {
  private final JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();
  private final AutonomyButtonSafetyModuleConfig autonomyButtonSafetyModuleConfig;
  
  public AutonomyButtonSafetyModule(AutonomyButtonSafetyModuleConfig autonomyButtonSafetyModuleConfig) {
    this.autonomyButtonSafetyModuleConfig = autonomyButtonSafetyModuleConfig;
  }
  //The put providers are implemented as anonymous classes
  //rimo
  private PutProvider<RimoPutEvent> rimoPutProvider = new PutProvider<RimoPutEvent>() {
    @Override
    public Optional<RimoPutEvent> putEvent() {
      final boolean buttonPushed = buttonPushed();
      return buttonPushed //
          ? Optional.empty()
          : StaticHelper.OPTIONAL_RIMO_PASSIVE;
    }
    
    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.SAFETY;
    }
  };
  //steering
  private PutProvider<SteerPutEvent> steeringPutProvider = new PutProvider<SteerPutEvent>() {
    @Override
    public Optional<SteerPutEvent> putEvent() {
      final boolean buttonPushed = buttonPushed();
      return buttonPushed //
          ? Optional.empty()
          : Optional.of(SteerPutEvent.PASSIVE_MOT_TRQ_0);
    }
    
    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.SAFETY;
    }
  };
  private PutProvider<LinmotPutEvent> linmotPutProvider = new PutProvider<LinmotPutEvent>() {
    @Override
    public Optional<LinmotPutEvent> putEvent() {
      //maybe we should actually brake
      final boolean buttonPushed = buttonPushed();
      return buttonPushed //
          ? Optional.empty()
          : Optional.of(
              LinmotPutOperation.INSTANCE.toRelativePosition(
                  autonomyButtonSafetyModuleConfig.brakingAmount));
    }
    
    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.SAFETY;
    }
  };
  
  @Override // from AbstractModule
  protected void first() throws Exception {
    SteerSocket.INSTANCE.addPutProvider(steeringPutProvider);
    LinmotSocket.INSTANCE.addPutProvider(linmotPutProvider);
    RimoSocket.INSTANCE.addPutProvider(rimoPutProvider);
    joystickLcmProvider.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(steeringPutProvider);
    LinmotSocket.INSTANCE.removePutProvider(linmotPutProvider);
    RimoSocket.INSTANCE.removePutProvider(rimoPutProvider);
    joystickLcmProvider.stopSubscriptions();
  }

  private boolean buttonPushed() {
    final Optional<JoystickEvent> joystick = joystickLcmProvider.getJoystick();
    if (joystick.isPresent()) { // is joystick button "autonomous" pressed?
      GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
      return gokartJoystickInterface.isAutonomousPressed();
    } else
      return false;
  }
}
