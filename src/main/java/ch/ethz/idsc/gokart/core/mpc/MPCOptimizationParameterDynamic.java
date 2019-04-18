// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** for the specification of:
 * speed limit
 * max acceleration along x axis
 * steering reg
 * moment of inertia (over mass) */
/* package */ class MPCOptimizationParameterDynamic implements MPCOptimizationParameter {
  private static final int LENGTH = 4 * 4;
  // ---
  private final Scalar speedLimit;
  private final Scalar xAccLimit;
  // TODO MH document steeringReg
  private final Scalar steeringReg;
  /** with unit "m" */
  private final Scalar specificMoI;

  public MPCOptimizationParameterDynamic(ByteBuffer byteBuffer) {
    speedLimit = Quantity.of(byteBuffer.getFloat(), SI.VELOCITY);
    xAccLimit = Quantity.of(byteBuffer.getFloat(), SI.ACCELERATION);
    steeringReg = RealScalar.of(byteBuffer.getFloat());
    specificMoI = Quantity.of(byteBuffer.getFloat(), SI.METER);
  }

  /** @param speedLimit with unit "m*s^-1"
   * @param xAccLimit with unit "m*s^-2"
   * @param steeringReg unitless
   * @param specificMoI with unit "m" */
  public MPCOptimizationParameterDynamic(Scalar speedLimit, Scalar xAccLimit, Scalar steeringReg, Scalar specificMoI) {
    this.speedLimit = speedLimit;
    this.xAccLimit = xAccLimit;
    this.steeringReg = steeringReg;
    this.specificMoI = specificMoI;
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(Magnitude.VELOCITY.toFloat(speedLimit));
    byteBuffer.putFloat(Magnitude.ACCELERATION.toFloat(xAccLimit));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(steeringReg));
    byteBuffer.putFloat(Magnitude.METER.toFloat(specificMoI));
  }

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }

  @Override // from MPCOptimizationParameter
  public Scalar speedLimit() {
    return speedLimit;
  }

  @Override // from MPCOptimizationParameter
  public Scalar xAccLimit() {
    return xAccLimit;
  }
}
