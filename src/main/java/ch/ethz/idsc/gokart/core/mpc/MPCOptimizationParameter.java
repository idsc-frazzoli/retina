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
  public final Scalar pureXAccLimit;
  public final Scalar pureRotAccEffect;
  public final Scalar torqueVecEffect;
  public final Scalar brakeeffect;

  // at the moment it is only for the speed limit
  public MPCOptimizationParameter(ByteBuffer byteBuffer) {
    // dummy constructor
    speedLimit = Quantity.of(byteBuffer.getFloat(), SI.VELOCITY);
    xAccLimit = Quantity.of(byteBuffer.getFloat(), SI.ACCELERATION);
    yAccLimit = Quantity.of(byteBuffer.getFloat(), SI.ACCELERATION);
    pureXAccLimit = Quantity.of(byteBuffer.getFloat(), SI.ACCELERATION);
    pureRotAccEffect = Quantity.of(byteBuffer.getFloat(), //
        SI.ACCELERATION.add(SI.ANGULAR_ACCELERATION.negate()));
    torqueVecEffect = Quantity.of(byteBuffer.getFloat(), //
        SI.ACCELERATION);
    brakeeffect = Quantity.of(byteBuffer.getFloat(), //
        SI.ONE);
  }

  public MPCOptimizationParameter(Scalar speedLimit) {
    this(speedLimit, Quantity.of(5, SI.ACCELERATION), Quantity.of(5, SI.ACCELERATION));
  }

  public MPCOptimizationParameter(Scalar speedLimit, Scalar xAccLimit, Scalar yAccLimit) {
    this(speedLimit, xAccLimit, yAccLimit, //
        Quantity.of(10, SI.ACCELERATION), Quantity.of(0, SI.ACCELERATION.add(SI.ANGULAR_ACCELERATION.negate())), Quantity.of(0, SI.ACCELERATION),
        Quantity.of(0, SI.ONE));
  }

  public MPCOptimizationParameter(Scalar speedLimit, Scalar xAccLimit, Scalar yAccLimit, //
      Scalar pureXAccLimit, Scalar pureRotAccEffect, Scalar torqueVecEffect, Scalar brakeEffect) {
    this.speedLimit = speedLimit;
    this.xAccLimit = xAccLimit;
    this.yAccLimit = yAccLimit;
    this.pureXAccLimit = pureXAccLimit;
    this.pureRotAccEffect = pureRotAccEffect;
    this.torqueVecEffect = torqueVecEffect;
    this.brakeeffect = brakeEffect;
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(Magnitude.VELOCITY.toFloat(speedLimit));
    byteBuffer.putFloat(Magnitude.ACCELERATION.toFloat(xAccLimit));
    byteBuffer.putFloat(Magnitude.ACCELERATION.toFloat(yAccLimit));
    byteBuffer.putFloat(Magnitude.ACCELERATION.toFloat(pureXAccLimit));
    byteBuffer.putFloat(pureRotAccEffect.number().floatValue());
    byteBuffer.putFloat(torqueVecEffect.number().floatValue());
    byteBuffer.putFloat(brakeeffect.number().floatValue());
  }

  @Override
  public int length() {
    return 7 * 4;
  }
}
