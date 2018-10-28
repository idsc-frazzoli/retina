// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCPathParameter implements MPCNativeInsertable {
  final Tensor controlPointsX;
  final Tensor controlPointsY;
  final Tensor controlPointsR;
  final int n;

  public MPCPathParameter(ByteBuffer byteBuffer) {
    n = byteBuffer.getInt();
    controlPointsX = Tensors.empty();
    controlPointsY = Tensors.empty();
    controlPointsR = Tensors.empty();
    for (int i = 0; i < n; i++) {
      controlPointsX.append(Quantity.of(byteBuffer.getFloat(), SI.METER));
      controlPointsY.append(Quantity.of(byteBuffer.getFloat(), SI.METER));
      controlPointsR.append(Quantity.of(byteBuffer.getFloat(), SI.METER));
    }
  }

  public MPCPathParameter(Tensor controlPointsX, Tensor controlPointsY, Tensor controlPointsR) {
    this.controlPointsX = controlPointsX;
    this.controlPointsY = controlPointsY;
    this.controlPointsR = controlPointsR;
    n = controlPointsX.length();
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    int n = controlPointsX.length();
    byteBuffer.putInt(n);
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

  @Override
  public int length() {
    return n * 4 * 3 + 4;
  }
}
