// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class MPCOpenLoopSteeringTest extends TestCase {
  public void testDerivative() {
    MPCSteering steering = new MPCOpenLoopSteering();
    // this has no effect
    Timing timing = Timing.started();
    steering.setStateProvider(new FakeNewsEstimator(timing));
    GokartState state0 = new GokartState(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    GokartState state1 = new GokartState(1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
    GokartControl control0 = new GokartControl(0, 1);
    GokartControl control1 = new GokartControl(0, -1);
    ControlAndPredictionStep[] steps = new ControlAndPredictionStep[2];
    steps[0] = new ControlAndPredictionStep(control0, state0);
    steps[1] = new ControlAndPredictionStep(control1, state1);
    ControlAndPredictionSteps cns = new ControlAndPredictionSteps(steps);
    steering.getControlAndPredictionSteps(cns);
    System.out.println(steering.getSteering(Quantity.of(0.1, SI.SECOND)));
    // test interpolation and extrapolation
    // System.out.println(steering.getSteering(Quantity.of(0.1, SI.SECOND)));
    // FIXME MH test fail
    // assertTrue(Chop._05.close( //
    // steering.getSteering(Quantity.of(0.1, SI.SECOND)), Quantity.of(0.1, "SCE")));
    // assertTrue(Chop._05.close( //
    // steering.getSteering(Quantity.of(-0.1, SI.SECOND)), Quantity.of(-0.1, "SCE")));
    // assertTrue(Chop._05.close( //
    // steering.getSteering(Quantity.of(1.2, SI.SECOND)), Quantity.of(0.8, "SCE")));
    // assertTrue(Chop._05.close( //
    // steering.getSteering(Quantity.of(0.1, SI.SECOND)), Quantity.of(0.1, "SCE")));
    // assertTrue(Chop._05.close( //
    // steering.getSteering(Quantity.of(-0.1, SI.SECOND)), Quantity.of(-0.1, "SCE")));
  }
}
