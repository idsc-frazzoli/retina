// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCPathParameter implements MPCNativeInsertable {
  // starting Progress designates the current position on the path. (0->middle point between first 2 control points)
  // starting Progress is in [0,N-2] where N is the number of control points.
  final Scalar startingProgress;
  final Tensor combinedControlPoints;

  public MPCPathParameter(ByteBuffer byteBuffer) {
    int n = byteBuffer.getInt();
    startingProgress = RealScalar.of(byteBuffer.getFloat());
    combinedControlPoints = Tensors.empty();
    for (int i = 0; i < n; ++i)
      combinedControlPoints.append(Tensors.of( //
          Quantity.of(byteBuffer.getFloat(), SI.METER), //
          Quantity.of(byteBuffer.getFloat(), SI.METER), //
          Quantity.of(byteBuffer.getFloat(), SI.METER) //
      ));
  }

  /** @param startingProgress progress on the spline where 0 -> position between the first 2 control points
   * @param controlPointsX control points for x
   * @param controlPointsY control points for y
   * @param controlPointsR control points for radius */
  public MPCPathParameter(Scalar startingProgress, Tensor combinedControlPoints) {
    this.startingProgress = startingProgress;
    this.combinedControlPoints = combinedControlPoints;
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    int n = combinedControlPoints.length();
    byteBuffer.putInt(n);
    byteBuffer.putFloat(startingProgress.number().floatValue());
    combinedControlPoints.flatten(-1) //
        .map(Scalar.class::cast) //
        .map(Scalar::number) //
        .forEach(number -> byteBuffer.putFloat(number.floatValue()));
  }

  Tensor getControlPointsX() {
    return combinedControlPoints.get(Tensor.ALL, 0);
  }

  Tensor getControlPointsY() {
    return combinedControlPoints.get(Tensor.ALL, 1);
  }

  Tensor getControlPointsR() {
    return combinedControlPoints.get(Tensor.ALL, 2);
  }

  public Scalar getProgressOnPath() {
    return startingProgress;
  }

  public int getN() {
    return combinedControlPoints.length();
  }

  @Override
  public int length() {
    return getN() * 4 * 3 + 4 + 4;
  }
}
