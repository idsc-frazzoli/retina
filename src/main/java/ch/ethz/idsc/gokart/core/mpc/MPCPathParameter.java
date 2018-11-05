// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCPathParameter implements MPCNativeInsertable {
  // starting Progress designates the current position on the path. (0->middle point between first 2 control points)
  // starting Progress is in [0,N-2] where N is the number of control points.
  final Scalar startingProgress;
  final Tensor controlPointsX;
  final Tensor controlPointsY;
  final Tensor controlPointsR;

  public MPCPathParameter(ByteBuffer byteBuffer) {
    int n = byteBuffer.getInt();
    startingProgress = Quantity.of(byteBuffer.getFloat(), SI.ONE);
    controlPointsX = Tensors.empty();
    controlPointsY = Tensors.empty();
    controlPointsR = Tensors.empty();
    for (int i = 0; i < n; i++) {
      controlPointsX.append(Quantity.of(byteBuffer.getFloat(), SI.METER));
      controlPointsY.append(Quantity.of(byteBuffer.getFloat(), SI.METER));
      controlPointsR.append(Quantity.of(byteBuffer.getFloat(), SI.METER));
    }
  }

  /** @param startingProgress progress on the spline where 0 -> position between the first 2 control points
   * @param controlPointsX control points for x
   * @param controlPointsY control points for y
   * @param controlPointsR control points for radius */
  public MPCPathParameter(Scalar startingProgress, Tensor controlPointsX, Tensor controlPointsY, Tensor controlPointsR) {
    this.startingProgress = startingProgress;
    this.controlPointsX = controlPointsX;
    this.controlPointsY = controlPointsY;
    this.controlPointsR = controlPointsR;
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    int n = controlPointsX.length();
    byteBuffer.putInt(n);
    byteBuffer.putFloat(startingProgress.number().floatValue());
    for (int i = 0; i < n; i++) {
      byteBuffer.putFloat(controlPointsX.Get(i).number().floatValue());
      byteBuffer.putFloat(controlPointsY.Get(i).number().floatValue());
      byteBuffer.putFloat(controlPointsR.Get(i).number().floatValue());
    }
  }

  public Tensor getControlPointsX() {
    return controlPointsX.copy();
  }

  public Tensor getControlPointsY() {
    return controlPointsY.copy();
  }

  public Tensor getControlPointsR() {
    return controlPointsR.copy();
  }

  public Scalar getProgressOnPath() {
    return startingProgress;
  }

  public int getN() {
    return controlPointsR.length();
  }

  @Override
  public int length() {
    return getN() * 4 * 3 + 4 + 4;
  }
}
