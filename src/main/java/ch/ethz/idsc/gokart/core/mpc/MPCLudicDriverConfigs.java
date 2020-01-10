package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.Scalar;

public enum MPCLudicDriverConfigs {
  BEGINNER( //
      RealScalar.of(0.02), // speed cost
      RealScalar.of(1), // lag Error
      RealScalar.of(0.12), // lat error
      RealScalar.of(0.1), // progress
      RealScalar.of(0.0012), // regularizer AB
      RealScalar.of(0.01), // regularizer TV
      RealScalar.of(10), // slack soft constraint
      RealScalar.of(5)), // max Speed
  MODERATE( //
      RealScalar.of(0.02), // speed cost
      RealScalar.of(1), // lag Error
      RealScalar.of(0.06), // lat error
      RealScalar.of(0.15), // progress
      RealScalar.of(0.0008), // regularizer AB
      RealScalar.of(0.01), // regularizer TV
      RealScalar.of(8), // slack soft constraint
      RealScalar.of(8)), // max Speed
  ADVANCED( //
      RealScalar.of(0.03), // speed cost
      RealScalar.of(1), // lag Error
      RealScalar.of(0.01), // lat error
      RealScalar.of(0.3), // progress
      RealScalar.of(0.0004), // regularizer AB
      RealScalar.of(0.01), // regularizer TV
      RealScalar.of(5), // slack soft constraint
      RealScalar.of(12)); // max Speed

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
