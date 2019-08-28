// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.OvalTrack;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class LaneKeepingCenterlineModuleTest extends TestCase {
  private static final Tensor CURVE = OvalTrack.SE2;

  public void testSimple() {
    LaneKeepingCenterlineModule laneKeepingCenterlineModule = new LaneKeepingCenterlineModule();
    laneKeepingCenterlineModule.launch();
    Tensor pose = CURVE.get(3);
    assertFalse(laneKeepingCenterlineModule.getCurve().isPresent());
    laneKeepingCenterlineModule.setCurve(Optional.of(CURVE));
    assertTrue(laneKeepingCenterlineModule.getCurve().isPresent());
    Optional<Clip> permittedRange = laneKeepingCenterlineModule.getPermittedRange(CURVE, pose);
    assertTrue(permittedRange.isPresent());
    Clip clip = permittedRange.get();
    Scalar width = clip.width();
    assertTrue(Scalars.lessThan(Quantity.of(0.2, "SCE"), width));
    assertTrue(Scalars.lessThan(width, Quantity.of(0.7, "SCE")));
    System.out.println(clip);
    laneKeepingCenterlineModule.runAlgo();
    laneKeepingCenterlineModule.terminate();
  }
}
