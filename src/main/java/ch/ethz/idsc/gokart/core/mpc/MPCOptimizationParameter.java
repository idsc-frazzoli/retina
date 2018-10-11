//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCOptimizationParameter implements MPCNativeInsertable {
  public final Scalar speedLimit;

  // TODO: actually implement this
  public MPCOptimizationParameter(ByteBuffer byteBuffer) {
    // dummy constructor
    speedLimit = Quantity.of(byteBuffer.getFloat(), SI.VELOCITY);
  }

  public MPCOptimizationParameter(Scalar speedLimit) {
    // dummy function
    this.speedLimit = speedLimit;
  }

  @Override
  public void input(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(Magnitude.VELOCITY.toFloat(speedLimit));
  }

  @Override
  public int getLength() {
    return 4;
  }
}
