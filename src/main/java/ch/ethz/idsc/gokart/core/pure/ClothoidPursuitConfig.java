// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.owl.bot.se2.glc.DynamicRatioLimit;
import ch.ethz.idsc.owl.bot.se2.glc.StaticRatioLimit;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;

public class ClothoidPursuitConfig extends PursuitConfig {
  public static final ClothoidPursuitConfig GLOBAL = AppResources.load(new ClothoidPursuitConfig());
  // ---
  public Boolean se2distance = false;
  public Boolean estimatePose = false;
  @FieldSubdivide(start = "0[s]", end = "0.1[s]", intervals = 100)
  public Scalar estimationTime = Quantity.of(0.015, SI.SECOND); // TODO JG (remove, ) test or learn online
  @FieldSubdivide(start = "0[m]", end = "10[m]", intervals = 20)
  public Scalar fallbackLookAhead = Quantity.of(7, SI.METER);
  @FieldSubdivide(start = "0[m]", end = "5[m]", intervals = 50)
  public Scalar lookAheadResolution = Quantity.of(2, SI.METER);

  public ClothoidPursuitConfig() {
    lookAhead = Quantity.of(3, SI.METER);
  }

  public Set<Scalar> lookAheads() {
    return IntStream.range(0, fallbackLookAhead.subtract(lookAhead).divide(lookAheadResolution).number().intValue() + 1) //
        .mapToObj(RealScalar::of).map(lookAheadResolution::multiply).map(lookAhead::add).collect(Collectors.toSet());
  }

  public static List<DynamicRatioLimit> ratioLimits() {
    // TODO maybe need to leave some margin to steering controller
    return Collections.singletonList(new StaticRatioLimit(SteerConfig.GLOBAL.turningRatioMax));
  }
}
