// code by mh, jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class MPCOpenLoopSteeringTest extends TestCase {
  public void testDerivative() {
    MPCSteering mpcSteering = new MPCOpenLoopSteering();
    // this has no effect
    Timing timing = Timing.started();
    // mpcSteering.setStateEstimationProvider(new FakeNewsEstimator(timing));
    GokartState state0 = new GokartState(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    GokartState state1 = new GokartState(1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
    GokartControl control0 = new GokartControl(0, 1);
    GokartControl control1 = new GokartControl(0, -1);
    ControlAndPredictionStep[] steps = new ControlAndPredictionStep[2];
    steps[0] = new ControlAndPredictionStep(control0, state0);
    steps[1] = new ControlAndPredictionStep(control1, state1);
    ControlAndPredictionSteps cns = new ControlAndPredictionSteps(steps);
    mpcSteering.getControlAndPredictionSteps(cns);
    assertEquals(mpcSteering.getSteering(Quantity.of(+0.1, SI.SECOND)).get(), Tensors.fromString("{+0.1[SCE], 1.0[SCE*s^-1]}"));
    assertEquals(mpcSteering.getSteering(Quantity.of(-0.1, SI.SECOND)).get(), Tensors.fromString("{-0.1[SCE], 1.0[SCE*s^-1]}"));
    assertEquals(mpcSteering.getSteering(Quantity.of(+1.2, SI.SECOND)).get(), Tensors.fromString("{0.8[SCE], -1.0[SCE*s^-1]}"));
    Optional<Tensor> optional = mpcSteering.getSteering(Quantity.of(2.2, SI.SECOND));
    assertFalse(optional.isPresent());
  }
}
