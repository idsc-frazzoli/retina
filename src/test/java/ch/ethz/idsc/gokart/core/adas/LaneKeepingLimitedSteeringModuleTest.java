// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.ToString;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class LaneKeepingLimitedSteeringModuleTest extends TestCase {
  public void testSimple() {
    LaneKeepingLimitedSteeringModule laneKeepingLimitedSteeringModule = new LaneKeepingLimitedSteeringModule();
    laneKeepingLimitedSteeringModule.launch();
    assertFalse(laneKeepingLimitedSteeringModule.putEvent().isPresent());
    laneKeepingLimitedSteeringModule.terminate();
  }

  private static final Tensor CURVE = DubendorfCurve.TRACK_OVAL_SE2;

  public void testSimple1() {
    LaneKeepingLimitedSteeringModule laneKeepingCenterlineModule = new LaneKeepingLimitedSteeringModule();
    laneKeepingCenterlineModule.launch();
    Tensor pose = CURVE.get(3);
    assertFalse(laneKeepingCenterlineModule.getCurve().isPresent());
    laneKeepingCenterlineModule.setCurve(Optional.of(CURVE));
    assertTrue(laneKeepingCenterlineModule.getCurve().isPresent());
    Optional<Clip> permittedRange = laneKeepingCenterlineModule.getPermittedRange(CURVE, pose);
    assertTrue(permittedRange.isPresent());
    Clip clip = permittedRange.get();
    Scalar width = clip.width();
    assertTrue(Scalars.lessThan(Quantity.of(0.3, "SCE"), width));
    assertTrue(Scalars.lessThan(width, Quantity.of(0.7, "SCE")));
    System.out.println(ToString.of(clip));
    laneKeepingCenterlineModule.runAlgo();
    laneKeepingCenterlineModule.terminate();
  }
}
