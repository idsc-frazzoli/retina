// code by gjoel
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

public class ClothoidPursuitConfig extends PursuitConfig {
  public static final ClothoidPursuitConfig GLOBAL = AppResources.load(new ClothoidPursuitConfig());
  // ---
  public Boolean se2distance = false;
  @FieldSubdivide(start = "0", end = "100", intervals = 100)
  public Scalar optimizationSteps = RealScalar.of(25);
  public Scalar scale = Quantity.of(20, "m*s");
  public Boolean estimatePose = false;
  @FieldSubdivide(start = "0[s]", end = "0.1[s]", intervals = 100)
  public Scalar estimationTime = Quantity.of(0.015, SI.SECOND); // TODO JG (remove, ) test or learn online

  public ClothoidPursuitConfig() {
    lookAhead = Quantity.of(5, SI.METER);
  }

  public int getOptimizationSteps() {
    return IntegerQ.require(optimizationSteps).number().intValue();
  }

  public final TrajectoryEntryFinder trajectoryEntryFinder = new InterpolationEntryFinder();

  public static List<DynamicRatioLimit> ratioLimits() {
    // TODO maybe need to leave some margin to steering controller
    return Collections.singletonList(new StaticRatioLimit(SteerConfig.GLOBAL.turningRatioMax));
  }
}
