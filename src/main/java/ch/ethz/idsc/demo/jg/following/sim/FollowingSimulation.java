// code by gjoel
package ch.ethz.idsc.demo.jg.following.sim;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitHelper;
import ch.ethz.idsc.gokart.core.pure.CurvePurePursuitHelper;
import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.gokart.core.pure.PursuitConfig;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.sca.Sign;

public class FollowingSimulation {
  private static final Scalar RATE = Quantity.of(10, SI.PER_SECOND);
  private static final Scalar DURATION = Quantity.of(60, SI.SECOND);
  private static final Scalar SPEED = Quantity.of(5, SI.VELOCITY);
  // ---
  private final static Tensor CURVE = DubendorfCurve.TRACK_OVAL_SE2; // TODO GJOEL implement
  // ---
  private final Map<String, Function<Tensor, Optional<Scalar>>> map = new HashMap<>();

  public FollowingSimulation() {
    map.put("pure", this::pure);
    map.put("clothoid", this::clothoid);
    export(CURVE, "reference");
  }

  /** @param initialPose of vehicle {[m], [m], [-]} */
  public void run(Tensor initialPose) {
    Scalar stepSize = RATE.reciprocal();
    int steps = DURATION.multiply(RATE).number().intValue();
    // ---
    map.forEach((name, function) -> {
      Tensor trail = Tensors.empty();
      Tensor ratios = Tensors.empty();
      Tensor pose = initialPose;
      Scalar ratio = Quantity.of(0, SI.PER_METER);
      for (int i = 0; i < steps; i++) {
        trail.append(pose);
        Optional<Scalar> optional = function.apply(pose);
        if (optional.isPresent())
          ratio = optional.get();
        ratios.append(ratio);
        pose = Se2CarIntegrator.INSTANCE.step(CarHelper.singleton(SPEED, ratio), pose, stepSize);
      }
      int idx_min = ArgMin.of(ratios);
      int idx_max = ArgMax.of(ratios);
      System.out.println(name + ":\tmin = " + ratios.Get(idx_min) + ", max = " + ratios.Get(idx_max));
      export(trail, name);
    });
  }

  /** set up pure pursuit
   * @param pose of vehicle {[m], [m], [-]}
   * @return ratio [m^-1] */
  private Optional<Scalar> pure(Tensor pose) {
    return CurvePurePursuitHelper.getRatio(pose, CURVE, Sign.isPositiveOrZero(SPEED), PursuitConfig.GLOBAL.lookAhead);
  }

  /** set up clothoid pursuit
   * @param pose of vehicle {[m], [m], [-]}
   * @return ratio [m^-1] */
  private Optional<Scalar> clothoid(Tensor pose) {
    return CurveClothoidPursuitHelper.getPlan(pose, SPEED, CURVE, //
        Sign.isPositiveOrZero(SPEED), //
        PursuitConfig.GLOBAL.trajectoryEntryFinder, //
        PursuitConfig.ratioLimits()).map(p -> p.ratio);
  }

  /** @param tensor to be exported
   * @param name -> name_trail.csv */
  private void export(Tensor tensor, String name) {
    try {
      Export.of(HomeDirectory.file(name + "_trail.csv"), Tensor.of(tensor.stream().map(PoseHelper::toUnitless)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    Tensor initialPose = CURVE.get(0); // TODO GJOEL randomize
    new FollowingSimulation().run(initialPose);
  }
}
