// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class MPCSimpleBrakingAndPowerTest extends TestCase {
  public void testDerivative() {
    MPCBraking braking = new MPCSimpleBraking();
    // this has no effect
    Stopwatch started = Stopwatch.started();
    braking.setStateProvider(new FakeNewsEstimator(started));
    GokartState state0 = new GokartState(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    GokartState state1 = new GokartState(1, 0, 0, 0, 0, 0, 0, 0, 0, 1);
    GokartState state2 = new GokartState(2, 0, 0, 0, 0, 0, 0, 0, 0, 1);
    GokartControl control0 = new GokartControl(1, 1);
    GokartControl control1 = new GokartControl(-1, -1);
    GokartControl control2 = new GokartControl(-4, -1);
    ControlAndPredictionStep[] steps = new ControlAndPredictionStep[3];
    steps[0] = new ControlAndPredictionStep(control0, state0);
    steps[1] = new ControlAndPredictionStep(control1, state1);
    steps[2] = new ControlAndPredictionStep(control2, state2);
    ControlAndPredictionSteps cns = new ControlAndPredictionSteps(steps);
    braking.getControlAndPredictionSteps(cns);
    //System.out.println(braking.getBraking(Quantity.of(0.1, SI.SECOND)));
    // test
    assertTrue(Chop._05.close(//
        braking.getBraking(Quantity.of(0.1, SI.SECOND)), Quantity.of(0, SI.ONE)));
    assertTrue(Chop._05.close(//
        braking.getBraking(Quantity.of(1.1, SI.SECOND)), Quantity.of(0, SI.ONE)));
    assertTrue(Scalars.lessThan(Quantity.of(0, SI.ONE),//
        braking.getBraking(Quantity.of(2.1, SI.SECOND))));
  }
}
