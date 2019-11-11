package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

class MPCOptimizationParameterLudic extends MPCOptimizationParameterDynamic {
  private static final int LENGTH = (4 + 16) * 4;
  /** Pacejka's formula front wheels parameters */
  public Scalar pacejkaFB = RealScalar.of(9);
  public Scalar pacejkaFC = RealScalar.of(1);
  public Scalar pacejkaFD = RealScalar.of(10);
  /** Pacejka's formula rear wheels parameters */
  public Scalar pacejkaRB = RealScalar.of(5.2);
  public Scalar pacejkaRC = RealScalar.of(1.1);
  public Scalar pacejkaRD = RealScalar.of(10);
  /** stiffness, damping, inertia of the steering column */
  // TODO Unit is SCT/SCE
  public Scalar steerStiff = RealScalar.of(0.8875);
  // TODO Unit is SCT*s/SCE
  public Scalar steerDamp = RealScalar.of(0.1625);
  // TODO Unit is SCT*s^2/SCE
  public Scalar steerInertia = RealScalar.of(0.0125);
  /** Parameters of the cost function (without unit of measure) */
  /** Lag Error */
  public Scalar lagError = RealScalar.of(1);
  /** Lateral Error */
  public Scalar latError = RealScalar.of(0.01);
  /** Path Progress */
  public Scalar progress = RealScalar.of(0.2);
  /** Regularizer for input AB */
  public Scalar regularizerAB = RealScalar.of(0.0004);
  /** Regularizer for speedCost */
  public Scalar speedCost = RealScalar.of(0.04);
  /** Slack variable for soft constraint */
  public Scalar slackSoftConstraint = RealScalar.of(5);
  /** Regularizer for input TV */
  public Scalar regularizerTV = RealScalar.of(0.01);

  public MPCOptimizationParameterLudic(ByteBuffer byteBuffer) {
    super(byteBuffer);
    pacejkaFB = RealScalar.of(byteBuffer.getFloat());
    pacejkaFC = RealScalar.of(byteBuffer.getFloat());
    pacejkaFD = RealScalar.of(byteBuffer.getFloat());
    pacejkaRB = RealScalar.of(byteBuffer.getFloat());
    pacejkaRC = RealScalar.of(byteBuffer.getFloat());
    pacejkaRD = RealScalar.of(byteBuffer.getFloat());
    steerStiff = RealScalar.of(byteBuffer.getFloat());
    steerDamp = RealScalar.of(byteBuffer.getFloat());
    steerInertia = RealScalar.of(byteBuffer.getFloat());
    lagError = RealScalar.of(byteBuffer.getFloat());
    latError = RealScalar.of(byteBuffer.getFloat());
    progress = RealScalar.of(byteBuffer.getFloat());
    regularizerAB = RealScalar.of(byteBuffer.getFloat());
    speedCost = RealScalar.of(byteBuffer.getFloat());
    slackSoftConstraint = RealScalar.of(byteBuffer.getFloat());
    regularizerTV = RealScalar.of(byteBuffer.getFloat());
  }

  public MPCOptimizationParameterLudic(Scalar mpcMaxSpeed, Scalar maxLonAcc, Scalar steeringReg, Scalar specificMoI) {
    super(mpcMaxSpeed, maxLonAcc, steeringReg, specificMoI);
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    super.insert(byteBuffer);
    // ---
    byteBuffer.putFloat(Magnitude.ONE.toFloat(pacejkaFB));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(pacejkaFC));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(pacejkaFD));
    // ---
    byteBuffer.putFloat(Magnitude.ONE.toFloat(pacejkaRB));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(pacejkaRC));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(pacejkaRD));
    // ---
    byteBuffer.putFloat(Magnitude.ONE.toFloat(steerStiff));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(steerDamp));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(steerInertia));
    // ---
    byteBuffer.putFloat(Magnitude.ONE.toFloat(lagError));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(latError));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(progress));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(regularizerAB));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(speedCost));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(slackSoftConstraint));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(regularizerTV));
  }

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }
}
