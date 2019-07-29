// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.owl.math.pursuit.AssistedCurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.PseudoSe2CurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.SphereSe2CurveIntersection;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
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
  @FieldSubdivide(start = "0[m]", end = "5[m]", intervals = 50)
  public Scalar lookAheadResolution = Quantity.of(2, SI.METER);
  /** leave some margin to steering controller
   * {@link SteerConfig#turningRatioMax} */
  public Scalar turningRatioMax = Quantity.of(0.45, SI.PER_METER);

  public ClothoidPursuitConfig() {
    lookAhead = Quantity.of(5, SI.METER);
  }

  public AssistedCurveIntersection getAssistedCurveIntersection() {
    return se2distance //
        ? new PseudoSe2CurveIntersection(lookAhead) //
        : new SphereSe2CurveIntersection(lookAhead);
  }

  /** @return stream of look ahead distances bigger/equals lookAhead and smaller than fallbackLookAhead, in lookAheadResolution steps */
  public Stream<Scalar> lookAheads() {
    return IntStream.range(0, fallbackLookAhead.subtract(lookAhead).divide(lookAheadResolution).number().intValue() + 1) //
        .mapToObj(RealScalar::of).map(lookAheadResolution::multiply).map(lookAhead::add);
  }

  public Clip ratioLimits() {
    return Clips.absolute(turningRatioMax);
  }
}
