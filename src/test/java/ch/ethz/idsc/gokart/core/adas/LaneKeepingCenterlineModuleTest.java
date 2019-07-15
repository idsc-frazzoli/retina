// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitModule;
import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class LaneKeepingCenterlineModuleTest extends TestCase {
  private static final Tensor CURVE = DubendorfCurve.TRACK_OVAL_SE2;

  public void testSimple() {
    LaneKeepingCenterlineModule laneKeepingCenterlineModule = new LaneKeepingCenterlineModule();
    laneKeepingCenterlineModule.first();
    CurveClothoidPursuitModule curveClothoidPursuitModule = new CurveClothoidPursuitModule(ClothoidPursuitConfig.GLOBAL);
    curveClothoidPursuitModule.launch();
    Tensor pose = Tensors.of( //
        Quantity.of(10000, SI.METER), //
        Quantity.of(10000, SI.METER), //
        RealScalar.of(0));
    Optional<Tensor> optionalCurve = Optional.of(Tensors.fromString("{{1[m], 1[m], 2}, {3[m], 2[m], 4}}"));
    Tensor curve = optionalCurve.get();
    laneKeepingCenterlineModule.setCurve(Optional.ofNullable(CURVE));
    Optional<Clip> permittedRange = laneKeepingCenterlineModule.getPermittedRange(curve, pose);
    // System.out.println(permittedRange);
    assertTrue(laneKeepingCenterlineModule.getCurve().isPresent());
    laneKeepingCenterlineModule.runAlgo();
    laneKeepingCenterlineModule.last();
  }

  public void testSimple4() {
    LeftLaneModule leftLaneModule = new LeftLaneModule();
    Optional<Tensor> curve = Optional.of(Tensors.fromString("{{1[m], 1[m], 2}, {3[m], 2[m], 4}}"));
    Tensor pose = Tensors.of( //
        Quantity.of(10000, SI.METER), //
        Quantity.of(10000, SI.METER), //
        RealScalar.of(0));
    GokartPoseEvent testEvent = GokartPoseEvents.create(pose, RealScalar.ONE);
    Scalar criticalDistance = Quantity.of(1, SI.METER);
    leftLaneModule.leftLane(curve, testEvent, criticalDistance);
    System.out.println(" ");
  }
}
