// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;

/** the controller has to be subscribed to rimo get events */
public abstract class RimoRateControllerWrap implements RimoGetListener {
  private RimoGetEvent rimoGetEvent = null;

  @Override // from RimoGetListener
  public final void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }

  /** @param rate_target desired average rate of left and right rear wheel in unit "rad*s^-1"
   * @param angle of steering without unit but with interpretation in radians
   * @return */
  public final Optional<RimoPutEvent> iterate(Scalar rate_target, Scalar angle) {
    if (Objects.nonNull(rimoGetEvent))
      return Optional.of(protected_getRimoPutEvent(rate_target, angle, rimoGetEvent));
    return Optional.empty();
  }

  /** @param rate_target desired average rate of left and right rear wheel in unit "rad*s^-1"
   * @param theta steering wheel angle without unit but with interpretation of radians
   * @param rimoGetEvent non-null
   * @return */
  protected abstract RimoPutEvent protected_getRimoPutEvent(Scalar rate_target, Scalar angle, RimoGetEvent rimoGetEvent);
}
