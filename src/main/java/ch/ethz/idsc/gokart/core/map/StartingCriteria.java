// code by mh
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.retina.util.Refactor;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO JPH refactor
@Refactor
public enum StartingCriteria {
  ;
  private static boolean isInFront(Tensor position, Tensor startPosition, Tensor startDirection) {
    Tensor diff = position.subtract(startPosition);
    Scalar normalDistance = (Scalar) diff.dot(startDirection);
    return Scalars.lessThan(Quantity.of(0, SI.METER), normalDistance);
  }

  private static Tensor standartPosition() {
    return Tensors.vector(41.6, 34.2).multiply(Quantity.of(1, SI.METER));
  }

  private static Tensor standartDirection() {
    return AngleVector.of(RealScalar.of(-2.25));
  }

  private static boolean isInFrontOfStandartLine(Tensor position) {
    return isInFront(position, standartPosition(), standartDirection());
  }

  public static boolean getLineTrigger(Tensor position, Tensor lastPosition) {
    return isInFrontOfStandartLine(position) && !isInFrontOfStandartLine(lastPosition);
  }
}
