// code by mh
package ch.ethz.idsc.gokart.core.track;

import java.util.Objects;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ class PositionalEndConstraint extends TrackConstraint {
  private Tensor wantedPosition = null;
  private Tensor wantedDirection = null;

  @Override // from TrackConstraint
  public void compute(Tensor controlpointsX, Tensor controlpointsY, Tensor radiusControlPoints) {
    int lastIndex = controlpointsX.length() - 1;
    int secondLastIndex = lastIndex - 1;
    Tensor first = Tensors.of(controlpointsX.Get(secondLastIndex), controlpointsY.Get(secondLastIndex));
    Tensor second = Tensors.of(controlpointsX.Get(lastIndex), controlpointsY.Get(lastIndex));
    Tensor startPos = Mean.of(Tensors.of(first, second));
    if (Objects.isNull(wantedPosition)) {
      wantedPosition = startPos;
      wantedDirection = Normalize.with(Norm._2).apply(second.subtract(first));
    }
    Tensor realVector = second.subtract(first);
    Scalar projection = (Scalar) Min.of(realVector.dot(wantedDirection), Quantity.of(0, SI.METER)).divide(RealScalar.of(2));
    Tensor correctedFirst = startPos.subtract(wantedDirection.multiply(projection));
    Tensor correctedSecond = startPos.add(wantedDirection.multiply(projection));
    setAll(controlpointsX, controlpointsY, radiusControlPoints);
    controlpointsX.set(correctedFirst.Get(0), secondLastIndex);
    controlpointsX.set(correctedSecond.Get(0), lastIndex);
    controlpointsY.set(correctedFirst.Get(1), secondLastIndex);
    controlpointsY.set(correctedSecond.Get(1), lastIndex);
  }
}