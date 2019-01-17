// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.lidar.LidarSpacialEvent;
import junit.framework.TestCase;

public class Vlp16PassiveSlowingTest extends TestCase {
  public void testFirstLast() throws Exception {
    Vlp16PassiveSlowing vlp16PassiveSlowing = new Vlp16PassiveSlowing();
    vlp16PassiveSlowing.first();
    vlp16PassiveSlowing.last();
  }

  public void testSimple() {
    Vlp16ClearanceModule vlp16ClearanceModule = new Vlp16PassiveSlowing();
    assertFalse(vlp16ClearanceModule.putEvent().isPresent()); // not calibrated
  }

  public void testEvents() throws Exception {
    Vlp16ClearanceModule vlp16ClearanceModule = new Vlp16PassiveSlowing();
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(0.1f);
    vlp16ClearanceModule.getEvent(gokartStatusEvent);
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    float[] coords = new float[3];
    // ---
    coords[0] = 0;
    coords[1] = 0;
    coords[2] = 0;
    vlp16ClearanceModule.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    // ---
    coords[0] = 0;
    coords[1] = 1;
    coords[2] = 0;
    vlp16ClearanceModule.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    // ---
    coords[0] = 1;
    coords[1] = 1;
    coords[2] = 0;
    vlp16ClearanceModule.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    // ---
    coords[0] = -1; // 1[m] along x axis in the back of the sensor
    coords[1] = 0;
    coords[2] = 0;
    vlp16ClearanceModule.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    // ---
    coords[0] = 1; // 1[m] along x axis in front of the sensor
    coords[1] = 0;
    coords[2] = 0;
    vlp16ClearanceModule.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertTrue(vlp16ClearanceModule.putEvent().isPresent());
    Thread.sleep(510);
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    coords[0] = 1; // 1[m] along x axis in front of the sensor
    coords[1] = 0.2f;
    coords[2] = (float) -0.8;
    vlp16ClearanceModule.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertTrue(vlp16ClearanceModule.putEvent().isPresent());
    Thread.sleep(510);
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
  }

  public void testEventsBypass() throws Exception {
    Vlp16PassiveSlowing vlp16PassiveSlowing = new Vlp16PassiveSlowing();
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(0.1f);
    vlp16PassiveSlowing.getEvent(gokartStatusEvent);
    assertFalse(vlp16PassiveSlowing.putEvent().isPresent());
    float[] coords = new float[3];
    // ---
    coords[0] = 0;
    coords[1] = 0;
    coords[2] = 0;
    vlp16PassiveSlowing.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vlp16PassiveSlowing.putEvent().isPresent());
    // ---
    coords[0] = 0;
    coords[1] = 1;
    coords[2] = 0;
    vlp16PassiveSlowing.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vlp16PassiveSlowing.putEvent().isPresent());
    // ---
    coords[0] = 1;
    coords[1] = 1;
    coords[2] = 0;
    vlp16PassiveSlowing.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vlp16PassiveSlowing.putEvent().isPresent());
    // ---
    coords[0] = -1; // 1[m] along x axis in the back of the sensor
    coords[1] = 0;
    coords[2] = 0;
    vlp16PassiveSlowing.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vlp16PassiveSlowing.putEvent().isPresent());
    // ---
    coords[0] = 1; // 1[m] along x axis in front of the sensor
    coords[1] = 0;
    coords[2] = 0;
    vlp16PassiveSlowing.bypassSafety();
    vlp16PassiveSlowing.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertFalse(vlp16PassiveSlowing.putEvent().isPresent());
    Thread.sleep(510);
    coords[0] = 1; // 1[m] along x axis in front of the sensor
    coords[1] = 0.2f;
    coords[2] = (float) -0.8;
    assertFalse(vlp16PassiveSlowing.putEvent().isPresent());
    vlp16PassiveSlowing.lidarSpacial(new LidarSpacialEvent(123, coords, (byte) 12));
    assertTrue(vlp16PassiveSlowing.putEvent().isPresent());
  }

  public void testCalibrationError() {
    Vlp16ClearanceModule vcm = new Vlp16PassiveSlowing();
    vcm.getEvent(new GokartStatusEvent(0.1f));
    assertFalse(vcm.putEvent().isPresent());
    vcm.getEvent(new GokartStatusEvent(Float.NaN));
    assertFalse(vcm.putEvent().isPresent());
  }

  public void testProviderRank() {
    Vlp16ClearanceModule vcm = new Vlp16PassiveSlowing();
    assertEquals(vcm.getProviderRank(), ProviderRank.EMERGENCY);
  }
}
