// code by mg
package ch.ethz.idsc.retina.app.slam.online;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.PurePursuitConfig;
import ch.ethz.idsc.gokart.core.pure.PursuitModule;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.owl.math.pursuit.PurePursuit;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** pure pursuit controller for SLAM algorithm */
public final class SlamCurvePurePursuitModule extends PursuitModule {
  private final Clip ratioClip = SteerConfig.GLOBAL.getRatioLimit();
  private Optional<Tensor> optionalCurve = Optional.empty();

  public SlamCurvePurePursuitModule() {
    super(PurePursuitConfig.GLOBAL);
  }

  @Override // form AbstractModule
  protected void protected_first() {
    // ---
  }

  @Override // from AbstractModule
  protected void protected_last() {
    // ---
  }

  @Override // form PursuitModule
  protected Optional<Scalar> deriveHeading() {
    Optional<Scalar> ratio = getRatio();
    if (ratio.isPresent()) { // is look ahead beacon available?
      if (ratioClip.isInside(ratio.get())) // is look ahead beacon within steering range?
        return Optional.of(ratio.get());
      System.err.println("beacon outside steering range");
    }
    return Optional.empty();
  }

  private Optional<Scalar> getRatio() {
    Optional<Tensor> optionalCurve = this.optionalCurve; // copy reference instead of synchronize
    if (optionalCurve.isPresent()) {
      PurePursuit purePursuit = PurePursuit.fromTrajectory( //
          optionalCurve.get(), SlamDvsConfig.eventCamera.slamPrcConfig.lookAheadMeter());
      return purePursuit.ratio().map(r -> Quantity.of(r, SI.PER_METER));
    }
    System.err.println("no curve in pure pursuit");
    return Optional.empty();
  }

  /** @param curve go kart frame coordinates */
  public void setCurve(Optional<Tensor> curve) {
    optionalCurve = curve;
  }
}
