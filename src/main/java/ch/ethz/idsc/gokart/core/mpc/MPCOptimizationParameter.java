// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.data.BufferInsertable;
import ch.ethz.idsc.tensor.Scalar;

public interface MPCOptimizationParameter extends BufferInsertable {
  /** @return positive value with unit "m*s^-1" */
  Scalar speedLimit();

  // not used
  /** @return max forward acceleration with unit "m*s^-2" */
  Scalar xAccLimit();
}
