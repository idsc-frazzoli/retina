// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.lidar.LidarSpacialEvent;
import junit.framework.TestCase;

public class Vlp16ActiveSlowingTest extends TestCase {
  public void testFirstLast() throws Exception {
    Vlp16ActiveSlowing vcm = new Vlp16ActiveSlowing();
    vcm.first();
    vcm.last();
  }

  public void testSimple() {
    Vlp16ClearanceModule vcm = new Vlp16ActiveSlowing();
    assertFalse(vcm.putEvent().isPresent());
    // assertEquals(vcm.putEvent().get(), RimoPutEvent.PASSIVE);
  }

  public void testEvents() throws Exception {
    Vlp16ClearanceModule vcm = new Vlp16ActiveSlowing();
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(0.1f);
    vcm.getEvent(gokartStatusEvent);
    assertFalse(vcm.putEvent().isPresent());
    float[] coords = new float[3];
    // ---
    coords[0] = 0;
    coords[1] = 0;
    coords[2] = 0;
    vcm.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vcm.putEvent().isPresent());
    // ---
    coords[0] = 0;
    coords[1] = 1;
    coords[2] = 0;
    vcm.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vcm.putEvent().isPresent());
    // ---
    coords[0] = 1;
    coords[1] = 1;
    coords[2] = 0;
    vcm.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vcm.putEvent().isPresent());
    // ---
    coords[0] = -1; // 1[m] along x axis in the back of the sensor
    coords[1] = 0;
    coords[2] = 0;
    vcm.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vcm.putEvent().isPresent());
    // ---
    coords[0] = 1; // 1[m] along x axis in front of the sensor
    coords[1] = 0;
    coords[2] = 0;
    vcm.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertTrue(vcm.putEvent().isPresent());
    Thread.sleep(510);
    assertFalse(vcm.putEvent().isPresent());
    coords[0] = 1; // 1[m] along x axis in front of the sensor
    coords[1] = 0.2f;
    coords[2] = (float) -0.8;
    vcm.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertTrue(vcm.putEvent().isPresent());
    Thread.sleep(510);
    assertFalse(vcm.putEvent().isPresent());
  }

  public void testCalibrationError() {
    Vlp16ClearanceModule vcm = new Vlp16ActiveSlowing();
    vcm.getEvent(new GokartStatusEvent(0.1f));
    assertFalse(vcm.putEvent().isPresent());
    vcm.getEvent(new GokartStatusEvent(Float.NaN));
    assertFalse(vcm.putEvent().isPresent());
  }

  public void testProviderRank() {
    Vlp16ClearanceModule vcm = new Vlp16ActiveSlowing();
    assertEquals(vcm.getProviderRank(), ProviderRank.EMERGENCY);
  }
}
