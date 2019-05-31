// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.gokart.calib.power.PredictiveMotorCurrents;

public final class PredictiveTorqueVectoring extends CalibratedTorqueVectoring {
  public PredictiveTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(new PredictiveMotorCurrents(torqueVectoringConfig));
  }
}
