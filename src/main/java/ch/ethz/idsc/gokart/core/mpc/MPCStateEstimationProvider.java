// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ abstract class MPCStateEstimationProvider {
  private final Timing timing;
  // default value
  protected Scalar pathProgress = RealScalar.ZERO;

  protected MPCStateEstimationProvider(Timing timing) {
    this.timing = timing;
  }

  /** get the time
   * 
   * @return time with unit "s" */
  protected Scalar getTime() {
    return Quantity.of(timing.seconds(), SI.SECOND);
  }

  /** get the newest state estimation *
   * 
   * @return the gokart state */
  public abstract GokartState getState();

  public void setPathProgress(Scalar pathProgress) {
    this.pathProgress = pathProgress;
  }

  /** first */
  abstract void first();

  /** last */
  abstract void last();
}
