// code by gjoel
package ch.ethz.idsc.demo.jg.following.sim;

import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum FollowingSimulator {
  ;

  private static final Scalar RATE = Quantity.of(10, SI.PER_SECOND);
  private static final Scalar DURATION = Quantity.of(60, SI.SECOND);
  private static final Scalar SPEED = Quantity.of(5, SI.VELOCITY);
  // ---
  private final static Tensor CURVE = DubendorfCurve.TRACK_OVAL_SE2; // TODO GJOEL implement

  /** @param tensor to be exported
   * @param name -> name_trail.csv */
  private static void export(Tensor tensor, String name) {
    try {
      Export.of(HomeDirectory.file(name + "_trail.csv"), Tensor.of(tensor.stream().map(PoseHelper::toUnitless)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    export(CURVE, "reference");
    Tensor initialPose = CURVE.get(0); // TODO GJOEl randomize
    for (FollowingSimulations simulation : FollowingSimulations.values()) {
      simulation.run(CURVE, initialPose, SPEED, DURATION, RATE.reciprocal());
      export(simulation.trail, simulation.toString().toLowerCase());
    }
  }
}
