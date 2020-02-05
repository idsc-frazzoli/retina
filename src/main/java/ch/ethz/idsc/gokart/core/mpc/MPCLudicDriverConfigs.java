// code by ta
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.Scalar;

public enum MPCLudicDriverConfigs {
  BEGINNER(//
      RealScalar.of(0.04), // speed cost
      RealScalar.of(1), // lag error
      RealScalar.of(0.03), // lat error
      RealScalar.of(0.15), // progress
      RealScalar.of(0.001), // regularizer AB
      RealScalar.of(0.01), // regularizer TV
      RealScalar.of(10), // slack soft-constraint
      RealScalar.of(5), // max speed
      RealScalar.of(1)), // regularizer tau (not used)
  MODERATE(//
      RealScalar.of(0.04), // speed cost
      RealScalar.of(1), // lag error
      RealScalar.of(0.01), // lat error
      RealScalar.of(0.15), // progress
      RealScalar.of(0.0006), // regularizer AB
      RealScalar.of(0.005), // regularizer TV
      RealScalar.of(8), // slack soft-constraint
      RealScalar.of(7), // max speed
      RealScalar.of(1)), // regularizer tau (not used)
  ADVANCED(//
      RealScalar.of(0.01), // speed cost
      RealScalar.of(1), // lag error
      RealScalar.of(0.015), // lat error
      RealScalar.of(0.2), // progress
      RealScalar.of(0.0004), // regularizer AB
      RealScalar.of(0.0075), // regularizer TV
      RealScalar.of(7), // slack soft-constraint
      RealScalar.of(9), // max speed
      RealScalar.of(1)), // regularizer tau (not used)
  BEGINNER_T(//
      RealScalar.of(0.04), // speed cost
      RealScalar.of(1), // lag error
      RealScalar.of(0.03), // lat error
      RealScalar.of(0.15), // progress
      RealScalar.of(0.001), // regularizer AB
      RealScalar.of(0.01), // regularizer TV
      RealScalar.of(10), // slack soft-constraint
      RealScalar.of(5), // max speed
      RealScalar.of(0.0005)), // regularizer tau
  MODERATE_T(//
      RealScalar.of(0.04), // speed cost
      RealScalar.of(1), // lag error
      RealScalar.of(0.01), // lat error
      RealScalar.of(0.15), // progress
      RealScalar.of(0.0006), // regularizer AB
      RealScalar.of(0.005), // regularizer TV
      RealScalar.of(8), // slack soft-constraint
      RealScalar.of(7), // max speed
      RealScalar.of(0.0005)), // regularizer tau
  ADVANCED_T(//
      RealScalar.of(0.005), // speed cost
      RealScalar.of(1), // lag error
      RealScalar.of(0.015), // lat error
      RealScalar.of(0.2), // progress
      RealScalar.of(0.0004), // regularizer AB
      RealScalar.of(0.0075), // regularizer TV
      RealScalar.of(7), // slack soft-constraint
      RealScalar.of(9), // max speed
      RealScalar.of(0.0005)); // regularizer tau

  private final MPCLudicConfig mpcLudicConfig;

  MPCLudicDriverConfigs(Scalar speedCost, Scalar lagError, Scalar latError, Scalar progress, //
      Scalar regularizerAB, Scalar regularizerTV, Scalar slackSoftConstraint, Scalar maxSpeed, //
      Scalar regularizerTau) {
    mpcLudicConfig = new MPCLudicConfig();
    mpcLudicConfig.speedCost = speedCost;
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
    mpcLudicConfig.regularizerTau = regularizerTau;
  }

  public MPCLudicConfig get() {
    return mpcLudicConfig;
  }
}
