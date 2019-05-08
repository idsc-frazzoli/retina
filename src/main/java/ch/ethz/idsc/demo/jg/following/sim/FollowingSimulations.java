// code by gjoel
package ch.ethz.idsc.demo.jg.following.sim;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitHelper;
import ch.ethz.idsc.gokart.core.pure.CurvePurePursuitHelper;
import ch.ethz.idsc.gokart.core.pure.PursuitConfig;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

public enum FollowingSimulations {
  PURE {
    @Override
    public Optional<Scalar> setup(Tensor pose, Scalar speed, Tensor curve) {
      return CurvePurePursuitHelper.getRatio(pose, curve, Sign.isPositiveOrZero(speed), PursuitConfig.GLOBAL.lookAhead);
    }
  },
  CLOTHOID {
    @Override
    public Optional<Scalar> setup(Tensor pose, Scalar speed, Tensor curve) {
      return CurveClothoidPursuitHelper.getPlan(pose, speed, curve, //
          Sign.isPositiveOrZero(speed), //
          PursuitConfig.GLOBAL.trajectoryEntryFinder, //
          PursuitConfig.ratioLimits()).map(p -> p.ratio);
    }
  };

  private Tensor trail = Tensors.empty();
  private Tensor ratios = Tensors.empty();

  /** @param curve reference
   * @param initialPose of vehicle {x[m], y[m], angle}
   * @param speed of vehicle [m*s^-1]
   * @param duration of simulation [s]
   * @param timeStep of simulation [s] */
  public void run(Tensor curve, Tensor initialPose, Scalar speed, Scalar duration, Scalar timeStep) {
    Tensor pose = initialPose;
    Scalar ratio = Quantity.of(0, SI.PER_METER);
    for (Scalar time = Quantity.of(0, SI.SECOND); Scalars.lessEquals(time, duration); time = time.add(timeStep)) {
      trail.append(pose);
      Optional<Scalar> optional = setup(pose, speed, curve);
      if (optional.isPresent())
        ratio = optional.get();
      ratios.append(ratio);
      pose = Se2CarIntegrator.INSTANCE.step(CarHelper.singleton(speed, ratio), pose, timeStep);
    }
  }

  /** @return vehicle trail {{x[m], y[m], angle}, ...} */
  public Optional<Tensor> trail() {
    if (Tensors.nonEmpty(trail))
      return Optional.of(trail);
    return Optional.empty();
  }

  /** @return ratios {[m^-1], ...} */
  public Optional<Tensor> ratios() {
    if (Tensors.nonEmpty(ratios))
      return Optional.of(ratios);
    return Optional.empty();
  }

  /** @return clip of min to max ratio [m^-1] */
  public Optional<Clip> ratioRange() {
    if (Tensors.nonEmpty(ratios)) {
      int idx_min = ArgMin.of(ratios);
      int idx_max = ArgMax.of(ratios);
      return Optional.of(Clips.interval(ratios.Get(idx_min), ratios.Get(idx_max)));
    }
    return Optional.empty();
  }

  /** @param pose of vehicle {x[m], y[m], angle}
   * @param speed of vehicle [m*s^-1]
   * @param curve reference
   * @return ratio [m^-1] */
  public abstract Optional<Scalar> setup(Tensor pose, Scalar speed, Tensor curve);
}
