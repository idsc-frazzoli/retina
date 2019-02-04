// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCActiveCompensationLearningConfig {
  public static final MPCActiveCompensationLearningConfig GLOBAL = AppResources.load(new MPCActiveCompensationLearningConfig());
  // ---
  /** how fast is the acceleration corrected scaled by acceleration */
  public Scalar negativeAccelerationCorrectionRate = Quantity.of(0.01, SI.ACCELERATION.negate().multiply(RealScalar.of(2)).add(SI.PER_SECOND));
  /** how fast is the steering corrected scaled by steering rate (rotation per meter driven [m^-1]) */
  public Scalar steeringCorrectionRate = Quantity.of(0.0, SI.SECOND.add(SI.METER).add(SI.PER_SECOND));// ->gives us [m]
  /** fixed correction rate for braking */
  public Scalar fixedCorrection = RealScalar.of(1.4);
}
