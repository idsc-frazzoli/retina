// code by jph
package ch.ethz.idsc.gokart.core;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Unprotect;
import junit.framework.TestCase;

public class DubendorfCurveTest extends TestCase {
  public void testDim1() {
    assertEquals(Unprotect.dimension1(DubendorfCurve.TRACK_OVAL_R2), 2);
    assertEquals(Unprotect.dimension1(DubendorfCurve.TRACK_OVAL_SE2), 3);
    PoseHelper.toUnitless(DubendorfCurve.TRACK_OVAL_SE2.get(2));
  }

  public void testUnits() {
    Magnitude.METER.apply(DubendorfCurve.TRACK_OVAL_R2.Get(0, 0));
    Magnitude.METER.apply(DubendorfCurve.TRACK_OVAL_R2.Get(0, 1));
    Magnitude.METER.apply(DubendorfCurve.TRACK_OVAL_SE2.Get(0, 0));
    Magnitude.METER.apply(DubendorfCurve.TRACK_OVAL_SE2.Get(0, 1));
  }
}
