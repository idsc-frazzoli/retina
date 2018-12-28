// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCOptimizationParameter implements MPCNativeInsertable {
  public final Scalar speedLimit;
  public final Scalar xAccLimit;
  public final Scalar yAccLimit;

  // at the moment it is only for the speed limit
  public MPCOptimizationParameter(ByteBuffer byteBuffer) {
    // dummy constructor
    speedLimit = Quantity.of(byteBuffer.getFloat(), SI.VELOCITY);
    xAccLimit = Quantity.of(byteBuffer.getFloat(), SI.VELOCITY);
    yAccLimit = Quantity.of(byteBuffer.getFloat(), SI.VELOCITY);
  }

  public MPCOptimizationParameter(Scalar speedLimit) {
    // dummy function
    this.speedLimit = speedLimit;
    this.xAccLimit = Quantity.of(5, SI.ACCELERATION);
    this.yAccLimit = Quantity.of(5, SI.ACCELERATION);
  }
  

  public MPCOptimizationParameter(Scalar speedLimit, Scalar xAccLimit, Scalar yAccLimit) {
    this.speedLimit = speedLimit;
    this.xAccLimit = xAccLimit;
    this.yAccLimit = yAccLimit;
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(Magnitude.VELOCITY.toFloat(speedLimit));
    byteBuffer.putFloat(Magnitude.ACCELERATION.toFloat(xAccLimit));
    byteBuffer.putFloat(Magnitude.ACCELERATION.toFloat(yAccLimit));
  }

  @Override
  public int length() {
    return 3*4;
  }
}
