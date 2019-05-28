// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.gokart.calib.power.SimpleMotorCurrents;

public final class NormalizedTorqueVectoring extends CalibratedTorqueVectoring {
  public NormalizedTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(new SimpleMotorCurrents(torqueVectoringConfig));
  }
}
