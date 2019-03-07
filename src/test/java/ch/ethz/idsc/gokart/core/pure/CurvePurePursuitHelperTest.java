// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class CurvePurePursuitHelperTest extends TestCase {
  public void testSpecific1() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 1}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve.OVAL, true, PursuitConfig.GLOBAL.lookAheadMeter());
    Scalar lookAhead = optional.get();
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(lookAhead);
    // assertTrue(Clip.function( // for look ahead 3.9[m]
    // Quantity.of(-0.018, "rad"), //
    // Quantity.of(-0.016, "rad")).isInside(angle));
    assertTrue(Clip.function( //
        Quantity.of(-0.014, "rad"), //
        Quantity.of(-0.013, "rad")).isInside(angle));
  }

  public void testSpecific2() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 0.9}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve.OVAL, true, PursuitConfig.GLOBAL.lookAheadMeter());
    Scalar lookAhead = optional.get();
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(lookAhead);
    assertTrue(Clip.function( //
        Quantity.of(0.04, "rad"), //
        Quantity.of(0.07, "rad")).isInside(angle));
  }

  public void testLookAheadFail() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 42.9[m], 2.9}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve.OVAL, true, PursuitConfig.GLOBAL.lookAheadMeter());
    assertFalse(optional.isPresent());
  }

  public void testLookAheadDistanceFail() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 420.9[m], 2.9}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve.OVAL, true, PursuitConfig.GLOBAL.lookAheadMeter());
    assertFalse(optional.isPresent());
  }

  public void testSpecificHLE() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 0.0}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve.HYPERLOOP_EIGHT, true, PursuitConfig.GLOBAL.lookAheadMeter());
    Scalar lookAhead = optional.get();
    Clip.function(0.062, 0.069).requireInside(lookAhead);
  }

  public void testSpecificHLE_R() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 0.0}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve.HYPERLOOP_EIGHT, false, PursuitConfig.GLOBAL.lookAheadMeter());
    Scalar lookAhead = optional.get();
    Clip.function(0.0096, 0.015).requireInside(lookAhead);
  }

  public void testSpecificHLER() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 3.1415926535897932385}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve.HYPERLOOP_EIGHT_REVERSE, true, PursuitConfig.GLOBAL.lookAheadMeter());
    Scalar lookAhead = optional.get();
    Clip.function(-0.015, -0.0096).requireInside(lookAhead);
  }

  public void testSpecificHLER_R() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 3.1415926535897932385}");
    Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, DubendorfCurve.HYPERLOOP_EIGHT_REVERSE, false, PursuitConfig.GLOBAL.lookAheadMeter());
    Scalar lookAhead = optional.get();
    Clip.function(-0.069, -0.062).requireInside(lookAhead);
  }
}
