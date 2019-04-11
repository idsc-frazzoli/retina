// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ abstract class MPCStateEstimationProvider {
  private final Timing timing;

  protected MPCStateEstimationProvider(Timing timing) {
    this.timing = timing;
  }

  /** @return time since start of MPC operation with unit "s" */
  protected final Scalar getTime() {
    return Quantity.of(timing.seconds(), SI.SECOND);
  }

  /** get the newest state estimation *
   * 
   * @return instance of {@link GokartState} */
  protected abstract GokartState getState();

  /** first */
  abstract void first();

  /** last */
  abstract void last();
}
