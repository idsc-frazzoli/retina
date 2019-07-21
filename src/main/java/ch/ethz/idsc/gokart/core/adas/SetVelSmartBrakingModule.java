// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerUno;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.gokart.lcm.VectorFloatBlob;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/** class is used to develop and test anti lock brake logic */
public class SetVelSmartBrakingModule extends AbstractModule implements PutProvider<LinmotPutEvent>, Vmu931ImuFrameListener {
  private final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final HapticSteerConfig hapticSteerConfig;
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.LINMOT_ANTILOCK);
  private final Vmu931ImuLcmClient vmu931imuLcmClient = new Vmu931ImuLcmClient();
  private Scalar currentAcceleration = Quantity.of(0, SI.ACCELERATION);
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.getProvider();
  final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerUno();
  RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    @Override
    public Optional<RimoPutEvent> putEvent() {
      Scalar speed = hapticSteerConfig.setVel.add(Quantity.of(1, SI.VELOCITY));
      Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
      if (optional.isPresent()) {
        accelerate = true;
      }
      if (accelerate) {
        return rimoRateControllerWrap.iterate(speed);
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.EMERGENCY;
    }
  };

  public SetVelSmartBrakingModule() {
    this(HapticSteerConfig.GLOBAL);
  }

  public SetVelSmartBrakingModule(HapticSteerConfig hapticSteerConfig) {
    this.hapticSteerConfig = hapticSteerConfig;
  }

  @Override // from AbstractModule
  protected void first() {
    vmu931imuLcmClient.addListener(this);
    vmu931imuLcmClient.startSubscriptions();
    LinmotSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addPutProvider(rimoPutProvider);
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
  }

  @Override // from AbstractModule
  protected void last() {
    vmu931imuLcmClient.stopSubscriptions();
    RimoSocket.INSTANCE.removePutProvider(rimoPutProvider);
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
    LinmotSocket.INSTANCE.removePutProvider(this);
  }

  public void getEvent(RimoGetEvent getEvent) {
    rimoGetEvent = getEvent;
    rimoRateControllerWrap.getEvent(getEvent);
  }

  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  // velocity is higher than setVel -> full stop
  private Scalar brakePosition = HapticSteerConfig.GLOBAL.fullBraking;
  private boolean fullStopping = false;
  private boolean accelerate = false;

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    rimoPutProvider.putEvent();
    if (lidarLocalizationModule != null) {
      if (Scalars.lessThan(hapticSteerConfig.setVel, lidarLocalizationModule.getVelocity().Get(0))) {
        accelerate = false;
        fullStopping = true;
      }
      if (fullStopping) {
        fullStopping = Scalars.lessThan(Quantity.of(0.1, SI.VELOCITY), lidarLocalizationModule.getVelocity().Get(0));
        return smartBraking(rimoGetEvent.getAngularRate_Y_pair(), lidarLocalizationModule.getVelocity());
      }
    }
    return Optional.empty();
  }

  /** @param angularRate_Y_pair
   * @param velocityOrigin
   * @return constant braking position */
  Optional<LinmotPutEvent> smartBraking(Tensor angularRate_Y_pair, Tensor velocityOrigin) {
    Scalar angularRate_Origin = velocityOrigin.Get(0).divide(RimoTireConfiguration._REAR.radius());
    Tensor angularRate_Origin_pair = Tensors.of(angularRate_Origin, angularRate_Origin);
    Tensor slip = angularRate_Origin_pair.subtract(angularRate_Y_pair); // vector of length 2 with entries of unit [s^-1]
    binaryBlobPublisher.accept(VectorFloatBlob.encode(Flatten.of(Tensors.of( //
        slip, brakePosition, velocityOrigin.Get(0), angularRate_Origin, angularRate_Y_pair, currentAcceleration))));
    System.out.println(slip.map(Round._3) + " " + brakePosition + " " + currentAcceleration + " " + velocityOrigin.Get(0).map(Round._3));
    // the brake cannot be constantly applied otherwise the brake motor heats up too much
    // there is a desired range for slip (in theory 0.1-0.25)
    // if the slip is outside this range, the position of the brake is increased/decreased
    // if (hapticSteerConfig.slipClip().isOutside(slip.Get(0)))
    for (int i = 0; i < 2; i++) {
      if (Scalars.lessThan(slip.Get(i), hapticSteerConfig.minSlipTheory.multiply(angularRate_Origin))) {
        brakePosition = Clips.unit().apply(brakePosition.add(HapticSteerConfig.GLOBAL.incrBraking));
      }
      if (Scalars.lessThan(hapticSteerConfig.maxSlipTheory.multiply(angularRate_Origin), slip.Get(i))) {
        brakePosition = Clips.unit().apply(brakePosition.subtract(HapticSteerConfig.GLOBAL.incrBraking));
      }
    }
    LinmotPutEvent relativePosition = LinmotPutOperation.INSTANCE.toRelativePosition(brakePosition);
    return Optional.of(relativePosition);
  }

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    currentAcceleration = SensorsConfig.GLOBAL.getPlanarVmu931Imu().accXY(vmu931ImuFrame).Get(0);
  }
}
