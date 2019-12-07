// code by ta, em
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

class MPCOptimizationParameterLudic extends MPCOptimizationParameterDynamic {
  private static final int LENGTH = (4 + 17) * 4;
  /** Pacejka's formula front wheels parameters */
  private final Scalar pacejkaFB;
  private final Scalar pacejkaFC;
  private final Scalar pacejkaFD;
  /** Pacejka's formula rear wheels parameters */
  private final Scalar pacejkaRB;
  private final Scalar pacejkaRC;
  private final Scalar pacejkaRD;
  /** stiffness, damping, inertia of the steering column */
  // TODO Unit is SCT/SCE
  public Scalar steerStiff = RealScalar.of(0.8875);
  // TODO Unit is SCT*s/SCE
  public Scalar steerDamp = RealScalar.of(0.1625);
  // TODO Unit is SCT*s^2/SCE
  public Scalar steerInertia = RealScalar.of(0.0125);
  /** Parameters of the cost function (without unit of measure) */
  /** Lag Error */
  private final Scalar lagError;
  /** Lateral Error */
  private final Scalar latError;
  /** Path Progress */
  private final Scalar progress;
  /** Regularizer for input AB */
  private final Scalar regularizerAB;
  /** Regularizer for speedCost */
  private final Scalar speedCost;
  /** Slack variable for soft constraint */
  private final Scalar slackSoftConstraint;
  /** Regularizer for input TV */
  private final Scalar regularizerTV;
  private final Scalar regTau;

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
    regTau = RealScalar.of(byteBuffer.getFloat());
  }

  public MPCOptimizationParameterLudic(Scalar mpcMaxSpeed, Scalar maxLonAcc, Scalar steeringReg, Scalar specificMoI, MPCLudicConfig mpcLudicConfig) {
    super(mpcMaxSpeed, maxLonAcc, steeringReg, specificMoI);
    speedCost = mpcLudicConfig.speedCost;
    lagError = mpcLudicConfig.lagError;
    latError = mpcLudicConfig.latError;
    progress = mpcLudicConfig.progress;
    regularizerAB = mpcLudicConfig.regularizerAB;
    regularizerTV = mpcLudicConfig.regularizerTV;
    regTau = mpcLudicConfig.regularizerTau;
    slackSoftConstraint = mpcLudicConfig.slackSoftConstraint;
    pacejkaFB = mpcLudicConfig.pacejkaFB;
    pacejkaFC = mpcLudicConfig.pacejkaFC;
    pacejkaFD = mpcLudicConfig.pacejkaFD;
    pacejkaRB = mpcLudicConfig.pacejkaRB;
    pacejkaRC = mpcLudicConfig.pacejkaRC;
    pacejkaRD = mpcLudicConfig.pacejkaRD;
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
    byteBuffer.putFloat(Magnitude.ONE.toFloat(regTau));
  }

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }
}
