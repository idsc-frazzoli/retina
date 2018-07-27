// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.WayPoint;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sin;

/** adaption of {@link PurePursuit} to work with SLAM algorithm
 * the input will consist of one or multiple {@link WayPoint} objects*/
public class SlamPurePursuit {
  private static final Scalar TWO = DoubleScalar.of(2);

  // method required for interpolation when more than one waypoint is availble
  public static Optional<Tensor> beacon(List<WayPoint> wayPoints) {
    // ..
    return Optional.empty();
  }

  /** @param lookAhead {x, y, ...} where x is positive
   * @return rate with interpretation rad*m^-1, or empty if the first coordinate
   * of the look ahead beacon is non-positive
   * @throws Exception if lookAhead has insufficient length */
  public static Optional<Scalar> ratioPositiveX(Tensor lookAhead) {
    Scalar x = lookAhead.Get(0);
    if (Sign.isPositive(x)) {
      Scalar angle = ArcTan.of(x, lookAhead.Get(1));
      // in the formula below, 2 is not a magic constant
      // but has an exact geometric interpretation
      return Optional.of(Sin.FUNCTION.apply(angle.multiply(TWO)).divide(x));
    }
    return Optional.empty();
  }
}
