// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.owl.math.pursuit.AssistedCurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.PseudoSe2CurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.SphereSe2CurveIntersection;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class ClothoidPursuitConfig extends PursuitConfig {
  public static final ClothoidPursuitConfig GLOBAL = AppResources.load(new ClothoidPursuitConfig());
  // ---
  public Boolean se2distance = false;
  @FieldSubdivide(start = "0[m]", end = "10[m]", intervals = 20)
  public Scalar fallbackLookAhead = Quantity.of(7, SI.METER);
  @FieldSubdivide(start = "0[m]", end = "1[m]", intervals = 20)
  public Scalar lookAheadResolution = Quantity.of(.5, SI.METER);
  /** leave some margin to steering controller
   * {@link SteerConfig#turningRatioMax} */
  public Scalar turningRatioMax = Quantity.of(0.45, SI.PER_METER);

  public ClothoidPursuitConfig() {
    lookAhead = Quantity.of(5, SI.METER);
  }

  public AssistedCurveIntersection getAssistedCurveIntersection() {
    return getAssistedCurveIntersection(lookAhead);
  }

  public AssistedCurveIntersection getAssistedCurveIntersection(Scalar lookAhead) {
    return se2distance //
        ? new PseudoSe2CurveIntersection(lookAhead) //
        : new SphereSe2CurveIntersection(lookAhead);
  }

  public Clip ratioLimits() {
    return Clips.absolute(turningRatioMax);
  }
}
