// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class MPCOptimizationParameterDynamic implements MPCOptimizationParameter {
  public static final int LENGTH = 1 * 4;
  // ---
  private final Scalar speedLimit;

  // at the moment it is only for the speed limit
  public MPCOptimizationParameterDynamic(ByteBuffer byteBuffer) {
    // dummy constructor
    speedLimit = Quantity.of(byteBuffer.getFloat(), SI.VELOCITY);
  }

  public MPCOptimizationParameterDynamic(Scalar speedLimit) {
    this.speedLimit = speedLimit;
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(Magnitude.VELOCITY.toFloat(speedLimit));
  }

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }
}
