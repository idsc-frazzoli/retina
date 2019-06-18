// code by jph and am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.gokart.lcm.VectorFloatBlob;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/** class is used to develop and test anti lock brake logic */
public class SetVelNotsmartBrakingModule extends AbstractModule implements LinmotPutProvider {
  private final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final HapticSteerConfig hapticSteerConfig;
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.LINMOT_ANTILOCK);

  public SetVelNotsmartBrakingModule() {
    this(HapticSteerConfig.GLOBAL);
  }

  public SetVelNotsmartBrakingModule(HapticSteerConfig hapticSteerConfig) {
    this.hapticSteerConfig = hapticSteerConfig;
  }

  @Override // from AbstractModule
  protected void first() {
    LinmotSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
    LinmotSocket.INSTANCE.removePutProvider(this);
  }

  @Override // from LinmotPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  // velocity is higher than setVel -> full stop
  private Scalar brakePosition = HapticSteerConfig.GLOBAL.fullBraking;
  private Boolean fullStopping = false;

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    if (lidarLocalizationModule != null) {
      if (Scalars.lessThan(hapticSteerConfig.setVel, lidarLocalizationModule.getVelocity().Get(0))) {
        fullStopping = true;
      }
      if (fullStopping) {
        fullStopping = Scalars.lessThan(Quantity.of(0.1, SI.VELOCITY), lidarLocalizationModule.getVelocity().Get(0)) //
            ? true
            : false;
        return notsmartBraking(rimoGetEvent.getAngularRate_Y_pair(), lidarLocalizationModule.getVelocity());
      }
    }
    return Optional.empty();
  }

  /** @param angularRate_Y_pair
   * @param velocityOrigin
   * @return constant braking position */
  Optional<LinmotPutEvent> notsmartBraking(Tensor angularRate_Y_pair, Tensor velocityOrigin) {
    Scalar angularRate_Origin = velocityOrigin.Get(0).divide(RimoTireConfiguration._REAR.radius());
    Tensor angularRate_Origin_pair = Tensors.of(angularRate_Origin, angularRate_Origin);
    Tensor slip = angularRate_Origin_pair.subtract(angularRate_Y_pair); // vector of length 2 with entries of unit [s^-1]
    binaryBlobPublisher.accept(VectorFloatBlob.encode(Flatten.of(Tensors.of( //
        slip, brakePosition, velocityOrigin.Get(0), angularRate_Origin))));
    System.out.println(slip.multiply(angularRate_Origin).map(Round._3) + " " + brakePosition + " " + velocityOrigin.Get(0).map(Round._3));
    return Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(hapticSteerConfig.fullBraking));
  }
}
