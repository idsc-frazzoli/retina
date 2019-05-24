// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.owl.bot.se2.glc.DynamicRatioLimit;
import ch.ethz.idsc.owl.bot.se2.glc.StaticRatioLimit;
import ch.ethz.idsc.owl.math.planar.InterpolationEntryFinder;
import ch.ethz.idsc.owl.math.planar.TrajectoryEntryFinder;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class ClothoidPursuitConfig extends PursuitConfig{
  public static final ClothoidPursuitConfig GLOBAL = AppResources.load(new ClothoidPursuitConfig());
  /***************************************************/
  @FieldSubdivide(start = "0[m]", end = "10[m]", intervals = 20)
  public Scalar lookAhead = Quantity.of(3, SI.METER);
  @FieldSubdivide(start = "0[m]", end = "10[m]", intervals = 20)
  public Scalar minDistance = Quantity.of(1, SI.METER);
  @FieldSubdivide(start = "0[m]", end = "1[m]", intervals = 10)
  public Scalar deviationLimit = Quantity.of(0.1, SI.METER);
  @FieldSubdivide(start = "0", end = "100", intervals = 100)
  public Scalar optimizationSteps = RealScalar.of(25);
  public Scalar scale = Quantity.of(20, "m*s");

  public ClothoidPursuitConfig() {
    super(Quantity.of(0.01, SI.SECOND));
  }

  public final TrajectoryEntryFinder trajectoryEntryFinder = new InterpolationEntryFinder();

  public static final List<DynamicRatioLimit> ratioLimits() {
    return Collections.singletonList(new StaticRatioLimit(SteerConfig.GLOBAL.turningRatioMax));
  }

  public int getOptimizationSteps() {
    return IntegerQ.require(optimizationSteps).number().intValue();
  }
}
