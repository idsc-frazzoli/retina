// code by am, jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerUno;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.gokart.lcm.VectorFloatBlob;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/** class is used to develop and test anti lock brake logic */
/* package */ class SetVelSimpleBrakingModule extends AntilockBrakeBaseModule {
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.LINMOT_ANTILOCK);
  private final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerUno();

  public SetVelSimpleBrakingModule() {
    this(HapticSteerConfig.GLOBAL);
  }

  public SetVelSimpleBrakingModule(HapticSteerConfig hapticSteerConfig) {
    super(hapticSteerConfig);
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

  // velocity is higher than setVel -> full stop
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

  Optional<RimoPutEvent> setVelEvent(Scalar MaxVel) {
    return rimoRateControllerWrap.iterate(MaxVel);
  }
}
