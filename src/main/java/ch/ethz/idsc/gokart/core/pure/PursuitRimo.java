// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerUno;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PursuitRimo extends PursuitBase<RimoPutEvent> {
  /** available implementations of RimoRateControllerWrap are
   * {@link RimoRateControllerUno}, and RimoRateControllerDuo
   * UNO uses a single PI-controller */
  public final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerUno();

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

  private Scalar speed = Quantity.of(0, SI.PER_SECOND);

  /** @param speed with unit "s^-1" */
  /* package */ void setSpeed(Scalar speed) {
    this.speed = speed;
  }

  /** @return speed with unit "s^-1" */
  /* package */ Scalar getSpeed() {
    return speed;
  }

  /***************************************************/
  @Override // from PurePursuitBase
  Optional<RimoPutEvent> control(SteerColumnInterface steerColumnInterface) {
    return rimoRateControllerWrap.iterate(speed);
  }

  @Override // from PurePursuitBase
  Optional<RimoPutEvent> fallback() {
    return Optional.empty();
  }
}
