// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConstants;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class CurvePurePursuitHelperTest extends TestCase {
  public void testSpecific1() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 1}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve2.OVAL, true, PurePursuitConfig.GLOBAL.lookAhead);
    Scalar lookAhead = optional.get();
    Scalar angle = RimoAxleConstants.steerAngleForTurningRatio(lookAhead);
    // assertTrue(Clip.function( // for look ahead 3.9[m]
    // Quantity.of(-0.018, ""), //
    // Quantity.of(-0.016, "")).isInside(angle));
    assertTrue(Clips.interval(-0.014, -0.013).isInside(angle));
  }

  public void testSpecific2() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 0.9}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve2.OVAL, true, PurePursuitConfig.GLOBAL.lookAhead);
    Scalar lookAhead = optional.get();
    Scalar angle = RimoAxleConstants.steerAngleForTurningRatio(lookAhead);
    assertTrue(Clips.interval(0.04, 0.07).isInside(angle));
  }

  public void testLookAheadFail() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 42.9[m], 2.9}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve2.OVAL, true, PurePursuitConfig.GLOBAL.lookAhead);
    assertFalse(optional.isPresent());
  }

  public void testLookAheadDistanceFail() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 420.9[m], 2.9}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve2.OVAL, true, PurePursuitConfig.GLOBAL.lookAhead);
    assertFalse(optional.isPresent());
  }

  public void testSpecificHLE() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 0.0}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve2.HYPERLOOP_EIGHT, true, PurePursuitConfig.GLOBAL.lookAhead);
    Scalar lookAhead = optional.get();
    Clips.interval(Quantity.of(0.062, SI.PER_METER), Quantity.of(0.069, SI.PER_METER)).requireInside(lookAhead);
  }

  public void testSpecificHLE_R() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 0.0}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve2.HYPERLOOP_EIGHT, false, PurePursuitConfig.GLOBAL.lookAhead);
    Scalar lookAhead = optional.get();
    Clips.interval(Quantity.of(0.0096, SI.PER_METER), Quantity.of(0.015, SI.PER_METER)).requireInside(lookAhead);
  }

  public void testSpecificHLER() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 3.1415926535897932385}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve2.HYPERLOOP_EIGHT_REVERSE, true, PurePursuitConfig.GLOBAL.lookAhead);
    Scalar lookAhead = optional.get();
    Clips.interval(Quantity.of(-0.015, SI.PER_METER), Quantity.of(-0.0096, SI.PER_METER)).requireInside(lookAhead);
  }

  public void testSpecificHLER_R() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 3.1415926535897932385}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve2.HYPERLOOP_EIGHT_REVERSE, false, PurePursuitConfig.GLOBAL.lookAhead);
    Scalar lookAhead = optional.get();
    Clips.interval(Quantity.of(-0.069, SI.PER_METER), Quantity.of(-0.062, SI.PER_METER)).requireInside(lookAhead);
  }
}
