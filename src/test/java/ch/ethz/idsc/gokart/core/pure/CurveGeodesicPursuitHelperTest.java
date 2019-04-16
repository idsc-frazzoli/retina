// code by jph, gjoel
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

import java.util.Optional;

public class CurveGeodesicPursuitHelperTest extends TestCase {
  // TODO add more tests

  public void testSpecific1() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 1}");
    Optional<Scalar> optional = CurveGeodesicPursuitHelper.getRatio(pose, RealScalar.ONE, DubendorfCurve.TRACK_OVAL_SE2, true, //
        PursuitConfig.GLOBAL.geodesic, PursuitConfig.GLOBAL.entryFinder, PursuitConfig.GLOBAL.ratioLimits);
    Scalar ratio = optional.get();
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(ratio);
    assertTrue(Clips.interval( //
        Quantity.of(-0.38, "rad"), //
        Quantity.of(-0.37, "rad")).isInside(angle));
  }

  public void testSpecific2() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 0.9}");
    Optional<Scalar> optional = CurveGeodesicPursuitHelper.getRatio(pose, RealScalar.ONE, DubendorfCurve.TRACK_OVAL_SE2, true, //
        PursuitConfig.GLOBAL.geodesic, PursuitConfig.GLOBAL.entryFinder, PursuitConfig.GLOBAL.ratioLimits);
    Scalar ratio = optional.get();
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(ratio);
    assertTrue(Clips.interval( //
        Quantity.of(-0.37, "rad"), //
        Quantity.of(-0.36, "rad")).isInside(angle));
  }
}
