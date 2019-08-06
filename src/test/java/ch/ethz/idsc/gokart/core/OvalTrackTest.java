// code by jph
package ch.ethz.idsc.gokart.core;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Unprotect;
import junit.framework.TestCase;

public class OvalTrackTest extends TestCase {
  public void testDim1() {
    assertEquals(Unprotect.dimension1(OvalTrack.R2), 2);
    assertEquals(Unprotect.dimension1(OvalTrack.SE2), 3);
    PoseHelper.toUnitless(OvalTrack.SE2.get(2));
  }

  public void testUnits() {
    Magnitude.METER.apply(OvalTrack.R2.Get(0, 0));
    Magnitude.METER.apply(OvalTrack.R2.Get(0, 1));
    Magnitude.METER.apply(OvalTrack.SE2.Get(0, 0));
    Magnitude.METER.apply(OvalTrack.SE2.Get(0, 1));
  }
}
