// code by jph
package ch.ethz.idsc.retina.util.sys;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class FailClockedModule extends AbstractClockedModule {
  @Override
  protected void first() {
    // ---
  }

  @Override
  protected void runAlgo() {
    throw new RuntimeException();
  }

  @Override
  protected void last() {
    // ---
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(0.01, SI.SECOND);
  }
}
