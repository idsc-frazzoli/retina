// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class MPCOptimizationParameterDynamic implements MPCOptimizationParameter {
  public static final int LENGTH = 2 * 4;
  // ---
  private final Scalar speedLimit;
  private final Scalar maxxAcc;

  // at the moment it is only for the speed limit
  public MPCOptimizationParameterDynamic(ByteBuffer byteBuffer) {
    // dummy constructor
    speedLimit = Quantity.of(byteBuffer.getFloat(), SI.VELOCITY);
    maxxAcc = Quantity.of(byteBuffer.getFloat(), SI.ACCELERATION);
  }

  public MPCOptimizationParameterDynamic(Scalar speedLimit, Scalar maxxAcc) {
    this.maxxAcc = maxxAcc;
    this.speedLimit = speedLimit;
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(Magnitude.VELOCITY.toFloat(speedLimit));
    byteBuffer.putFloat(Magnitude.ACCELERATION.toFloat(maxxAcc));
  }

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }
}
