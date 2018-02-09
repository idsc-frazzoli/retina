// code by jph
package ch.ethz.idsc.retina.dev.rimo;

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
      // TODO DUBENDORF remove try below after testing on gokart
      try {
        return Optional.of(protected_getRimoPutEvent(rate_target, angle, rimoGetEvent));
      } catch (Exception exception) {
        System.err.println("RRCW:" + exception.getMessage()); // message may be null
      }
    return Optional.empty();
  }

  /** @param rate_target desired average rate of left and right rear wheel in unit "rad*s^-1"
   * @param theta steering wheel angle without unit but with interpretation of radians
   * @param rimoGetEvent non-null
   * @return */
  protected abstract RimoPutEvent protected_getRimoPutEvent(Scalar rate_target, Scalar angle, RimoGetEvent rimoGetEvent);
}
