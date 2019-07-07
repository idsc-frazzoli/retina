// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.owl.bot.se2.glc.DynamicRatioLimit;
import ch.ethz.idsc.owl.bot.se2.glc.StaticRatioLimit;
import ch.ethz.idsc.owl.math.pursuit.AssistedCurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.PseudoSe2CurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.SphereSe2CurveIntersection;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;

public class ClothoidPursuitConfig extends PursuitConfig {
  public static final ClothoidPursuitConfig GLOBAL = AppResources.load(new ClothoidPursuitConfig());
  // ---
  public Boolean se2distance = false;
  public Boolean estimatePose = false;
  @FieldSubdivide(start = "0[s]", end = "0.1[s]", intervals = 100)
  public Scalar estimationTime = Quantity.of(0.015, SI.SECOND); // TODO GJOEL (remove, ) test or learn online
  @FieldSubdivide(start = "0[m]", end = "10[m]", intervals = 20)
  public Scalar fallbackLookAhead = Quantity.of(5, SI.METER);
  @FieldSubdivide(start = "0[m]", end = "1[m]", intervals = 20)
  public Scalar lookAheadResolution = Quantity.of(.5, SI.METER);
  /** leave some margin to steering controller
   * {@link SteerConfig#turningRatioMax} */
  public Scalar turningRatioMax = Quantity.of(0.45, SI.PER_METER);

  public ClothoidPursuitConfig() {
    lookAhead = Quantity.of(3, SI.METER);
  }

  public AssistedCurveIntersection getAssistedCurveIntersection() {
    return se2distance //
        ? new PseudoSe2CurveIntersection(lookAhead) //
        : new SphereSe2CurveIntersection(lookAhead);
  }

  public List<DynamicRatioLimit> ratioLimits() {
    return Collections.singletonList(new StaticRatioLimit(turningRatioMax));
  }
}
