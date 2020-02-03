// code by ta
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.Scalar;

public enum MPCLudicDriverConfigs {
  BEGINNER(//
      RealScalar.of(0.04), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.03), // Lat Error
      RealScalar.of(0.15), // Progress
      RealScalar.of(0.001), // Regularizer AB
      RealScalar.of(0.01), // Regularizer TV
      RealScalar.of(10), // Slack SoftConstraint
      RealScalar.of(5), // Max Speed
      RealScalar.of(1)), // Regtau Not used
  MODERATE(//
      RealScalar.of(0.04), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.01), // Lat Error
      RealScalar.of(0.15), // Progress
      RealScalar.of(0.0006), // Regularizer AB
      RealScalar.of(0.005), // Regularizer TV
      RealScalar.of(8), // slack SoftConstraint
      RealScalar.of(7), // Max Speed
      RealScalar.of(1)), // Regtau Not used
  ADVANCED(//
      RealScalar.of(0.01), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.015), // Lat Error
      RealScalar.of(0.2), // Progress
      RealScalar.of(0.0004), // Regularizer AB
      RealScalar.of(0.0075), // Regularizer TV
      RealScalar.of(7), // Slack SoftConstraint
      RealScalar.of(9), // Max Speed
      RealScalar.of(1)), // Regtau Not used
  BEGINNER_T(//
      RealScalar.of(0.04), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.03), // Lat Error
      RealScalar.of(0.15), // Progress
      RealScalar.of(0.001), // Regularizer AB
      RealScalar.of(0.01), // Regularizer TV
      RealScalar.of(10), // Slack SoftConstraint
      RealScalar.of(5), // Max Speed
      RealScalar.of(0.0005)), // Regtau
  MODERATE_T(//
      RealScalar.of(0.04), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.01), // Lat Error
      RealScalar.of(0.15), // Progress
      RealScalar.of(0.0006), // Regularizer AB
      RealScalar.of(0.005), // Regularizer TV
      RealScalar.of(8), // slack SoftConstraint
      RealScalar.of(7), // Max Speed
      RealScalar.of(0.0005)), // Regtau
  ADVANCED_T(//
      RealScalar.of(0.005), // Speed cost
      RealScalar.of(1), // Lag Error
      RealScalar.of(0.015), // Lat Error
      RealScalar.of(0.2), // Progress
      RealScalar.of(0.0004), // Regularizer AB
      RealScalar.of(0.0075), // Regularizer TV
      RealScalar.of(7), // Slack SoftConstraint
      RealScalar.of(9), // Max Speed
      RealScalar.of(0.0005)); // Regtau

  private final MPCLudicConfig mpcLudicConfig;

  MPCLudicDriverConfigs(Scalar speedcost, Scalar lagError, Scalar latError, Scalar progress,//
      Scalar regularizerAB, Scalar regularizerTV, Scalar slackSoftConstraint, Scalar maxSpeed,//
      Scalar regularizerTau) {
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
    mpcLudicConfig.regularizerTau=MPCLudicConfig.GLOBAL.regularizerTau;
  }

  public MPCLudicConfig get() {
    return mpcLudicConfig;
  }
}
