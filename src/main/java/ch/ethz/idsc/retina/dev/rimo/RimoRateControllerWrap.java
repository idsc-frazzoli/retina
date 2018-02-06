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

  /** @param pair vector of length == 2 with desired angular rates on the left and right rear wheel
   * @return */
  public final Optional<RimoPutEvent> iterate(Scalar vel_target, Scalar theta) {
    if (Objects.nonNull(rimoGetEvent))
      try {
        return Optional.of(protected_getRimoPutEvent(vel_target, theta, rimoGetEvent));
      } catch (Exception exception) {
        System.err.println("RRCW:" + exception.getMessage()); // message may be null
      }
    return Optional.empty();
  }

  /** @param vel_target with unit "rad*s^-1"
   * @param theta steering wheel angle without unit but with interpretation of radians
   * @param rimoGetEvent non-null
   * @return */
  protected abstract RimoPutEvent protected_getRimoPutEvent(Scalar vel_target, Scalar theta, RimoGetEvent rimoGetEvent);
}
