// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerDuo;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerUno;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

class PurePursuitRimo extends PurePursuitBase<RimoPutEvent> {
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  /** available implementations of RimoRateControllerWrap are
   * {@link RimoRateControllerUno}, and {@link RimoRateControllerDuo}
   * UNO uses a single PI-controller */
  /* package */ final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerUno();

  @Override // from StartAndStoppable
  public void start() {
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(rimoRateControllerWrap);
  }

  @Override // from StartAndStoppable
  public void stop() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(rimoRateControllerWrap);
  }

  private Scalar speed = Quantity.of(0, "rad*s^-1");

  /** @param speed with unit "rad*s^-1" */
  /* package */ void setSpeed(Scalar speed) {
    this.speed = speed;
  }

  /** @return speed with unit "rad*s^-1" */
  /* package */ Scalar getSpeed() {
    return speed;
  }

  /***************************************************/
  @Override // from PurePursuitBase
  Optional<RimoPutEvent> control(SteerColumnInterface steerColumnInterface) {
    return rimoRateControllerWrap.iterate( //
        speed, // average target velocity
        steerMapping.getAngleFromSCE(steerColumnInterface)); // steering angle
  }

  @Override // from PurePursuitBase
  Optional<RimoPutEvent> fallback() {
    return Optional.empty();
  }
}
