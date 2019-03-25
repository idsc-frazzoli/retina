// code by mh, jph
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum TestHelper {
  ;
  public static MPCOptimizationParameterKinematic optimizationParameterKinematic(Scalar speedLimit, Scalar xAccLimit, Scalar yAccLimit) {
    return new MPCOptimizationParameterKinematic( //
        speedLimit, //
        xAccLimit, //
        yAccLimit, //
        Quantity.of(10, SI.ACCELERATION), //
        Quantity.of(0, SI.ACCELERATION.add(SI.ANGULAR_ACCELERATION.negate())), //
        Quantity.of(0, SI.ACCELERATION), Quantity.of(0, SI.ONE));
  }
}
