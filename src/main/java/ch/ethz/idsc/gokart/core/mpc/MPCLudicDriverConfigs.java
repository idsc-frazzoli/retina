package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.Scalar;

public enum MPCLudicDriverConfigs {
  BEGINNER(//
      RealScalar.of(0.02), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.12), // Lat Error
      RealScalar.of(0.1), // Progress
      RealScalar.of(0.0012), // Regularizer AB
      RealScalar.of(0.01), // Regularizer TV
      RealScalar.of(10), // Slack SoftConstraint
      RealScalar.of(6)), // Max Speed
  MODERATE(//
      RealScalar.of(0.02), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.06), // Lat Error
      RealScalar.of(0.15), // Progress
      RealScalar.of(0.0008), // Regularizer AB
      RealScalar.of(0.01), // Regularizer TV
      RealScalar.of(8), // slack SoftConstraint
      RealScalar.of(10)), // Max Speed
  ADVANCED(//
      RealScalar.of(0.03), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.04), // Lat Error
      RealScalar.of(0.3), // Progress
      RealScalar.of(0.0006), // Regularizer AB
      RealScalar.of(0.01), // Regularizer TV
      RealScalar.of(5), // Slack SoftConstraint
      RealScalar.of(14)), // Max Speed
  BEGINNER_T(//
      RealScalar.of(0.02), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.12), // Lat Error
      RealScalar.of(0.1), // Progress
      RealScalar.of(0.0012), // Regularizer AB
      RealScalar.of(0.01), // Regularizer TV
      RealScalar.of(10), // Slack SoftConstraint
      RealScalar.of(6)), // Max Speed
  MODERATE_T(//
      RealScalar.of(0.02), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.10), // Lat Error
      RealScalar.of(0.15), // Progress
      RealScalar.of(0.0008), // Regularizer AB
      RealScalar.of(0.01), // Regularizer TV
      RealScalar.of(8), // slack SoftConstraint
      RealScalar.of(10)), // Max Speed
  ADVANCED_T(//
      RealScalar.of(0.02), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.08), // Lat Error
      RealScalar.of(0.3), // Progress
      RealScalar.of(0.0006), // Regularizer AB
      RealScalar.of(0.01), // Regularizer TV
      RealScalar.of(5), // Slack SoftConstraint
      RealScalar.of(14)); // Max Speed

  private final MPCLudicConfig mpcLudicConfig;

  MPCLudicDriverConfigs(Scalar speedcost, Scalar lagError, Scalar latError, Scalar progress,//
      Scalar regularizerAB, Scalar regularizerTV, Scalar slackSoftConstraint, Scalar maxSpeed) {
    mpcLudicConfig = new MPCLudicConfig();
    mpcLudicConfig.speedCost = speedcost;
    mpcLudicConfig.lagError = lagError;
    mpcLudicConfig.latError = latError;
    mpcLudicConfig.progress = progress;
    mpcLudicConfig.regularizerAB = regularizerAB;
    mpcLudicConfig.regularizerTV = regularizerTV;
    mpcLudicConfig.slackSoftConstraint = slackSoftConstraint;
    mpcLudicConfig.pacejkaRD = MPCLudicConfig.GLOBAL.pacejkaRD;
    mpcLudicConfig.pacejkaFD = MPCLudicConfig.GLOBAL.pacejkaFD;
    mpcLudicConfig.pacejkaRC = MPCLudicConfig.GLOBAL.pacejkaRC;
    mpcLudicConfig.pacejkaFC = MPCLudicConfig.GLOBAL.pacejkaFC;
    mpcLudicConfig.pacejkaRB = MPCLudicConfig.GLOBAL.pacejkaRB;
    mpcLudicConfig.pacejkaFB = MPCLudicConfig.GLOBAL.pacejkaFB;
    mpcLudicConfig.steerStiff = MPCLudicConfig.GLOBAL.steerStiff;
    mpcLudicConfig.steerDamp = MPCLudicConfig.GLOBAL.steerDamp;
    mpcLudicConfig.steerInertia = MPCLudicConfig.GLOBAL.steerInertia;
    mpcLudicConfig.maxSpeed = Quantity.of(maxSpeed, SI.VELOCITY);
  }

  public MPCLudicConfig get() {
    return mpcLudicConfig;
  }
}
