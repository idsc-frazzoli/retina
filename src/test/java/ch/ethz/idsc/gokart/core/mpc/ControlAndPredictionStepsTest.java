// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class ControlAndPredictionStepsTest extends TestCase {
  public void testSimple() {
    GokartState state0 = new GokartState(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    GokartState state1 = new GokartState(1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
    GokartState state2 = new GokartState(2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
    GokartControl control0 = new GokartControl(1, 1);
    GokartControl control1 = new GokartControl(-1, -1);
    GokartControl control2 = new GokartControl(-4, -1);
    ControlAndPredictionStep[] steps = new ControlAndPredictionStep[3];
    steps[0] = new ControlAndPredictionStep(control0, state0);
    steps[1] = new ControlAndPredictionStep(control1, state1);
    steps[2] = new ControlAndPredictionStep(control2, state2);
    ControlAndPredictionSteps cns = new ControlAndPredictionSteps(steps);
    assertEquals(cns.length(), 64 * 3);
    assertEquals(Dimensions.of(cns.toPositions()), Arrays.asList(3, 2));
    assertEquals(Dimensions.of(cns.toXYA()), Arrays.asList(3, 3));
    assertEquals(cns.toAccelerations(), Tensors.fromString("{1.0[m*s^-2], -1.0[m*s^-2], -4.0[m*s^-2]}"));
  }
}
