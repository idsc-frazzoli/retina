// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public abstract class MPCStateEstimationProvider {
  private final Stopwatch stopwatch;

  protected MPCStateEstimationProvider(Stopwatch stopwatch) {
    this.stopwatch = stopwatch;
  }

  /** get the time
   * 
   * @return time with unit "s" */
  protected Scalar getTime() {
    return Quantity.of(//
        stopwatch.display_seconds(), //
        SI.SECOND);
  }

  /** get the newest state estimation *
   * 
   * @return the gokart state */
  public abstract GokartState getState();

  /** first */
  abstract void first();

  /** last */
  abstract void last();
}
