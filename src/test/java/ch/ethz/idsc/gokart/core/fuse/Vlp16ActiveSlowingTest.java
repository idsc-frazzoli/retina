// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.lidar.LidarXYZEvent;
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
    Vlp16ClearanceModule vlp16ClearanceModule = new Vlp16ActiveSlowing();
    SteerColumnEvent steerColumnEvent = new SteerColumnEvent(0.1f);
    vlp16ClearanceModule.getEvent(steerColumnEvent);
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    float[] coords = new float[3];
    // ---
    coords[0] = 0;
    coords[1] = 0;
    coords[2] = 0;
    vlp16ClearanceModule.lidarSpacial(new LidarXYZEvent(123, coords, (byte) 12));
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    // ---
    coords[0] = 0;
    coords[1] = 1;
    coords[2] = 0;
    vlp16ClearanceModule.lidarSpacial(new LidarXYZEvent(123, coords, (byte) 12));
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    // ---
    coords[0] = 1;
    coords[1] = 1;
    coords[2] = 0;
    vlp16ClearanceModule.lidarSpacial(new LidarXYZEvent(123, coords, (byte) 12));
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    // ---
    coords[0] = -1; // 1[m] along x axis in the back of the sensor
    coords[1] = 0;
    coords[2] = 0;
    vlp16ClearanceModule.lidarSpacial(new LidarXYZEvent(123, coords, (byte) 12));
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    // ---
    coords[0] = 1; // 1[m] along x axis in front of the sensor
    coords[1] = 0;
    coords[2] = 0;
    vlp16ClearanceModule.lidarSpacial(new LidarXYZEvent(123, coords, (byte) 12));
    assertTrue(vlp16ClearanceModule.putEvent().isPresent());
    Thread.sleep(510);
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
    coords[0] = 1; // 1[m] along x axis in front of the sensor
    coords[1] = 0.2f;
    coords[2] = (float) -0.8;
    vlp16ClearanceModule.lidarSpacial(new LidarXYZEvent(123, coords, (byte) 12));
    assertTrue(vlp16ClearanceModule.putEvent().isPresent());
    Thread.sleep(510);
    assertFalse(vlp16ClearanceModule.putEvent().isPresent());
  }

  public void testCalibrationError() {
    Vlp16ClearanceModule vcm = new Vlp16ActiveSlowing();
    vcm.getEvent(new SteerColumnEvent(0.1f));
    assertFalse(vcm.putEvent().isPresent());
    vcm.getEvent(new SteerColumnEvent(Float.NaN));
    assertFalse(vcm.putEvent().isPresent());
  }

  public void testProviderRank() {
    Vlp16ClearanceModule vcm = new Vlp16ActiveSlowing();
    assertEquals(vcm.getProviderRank(), ProviderRank.EMERGENCY);
  }
}
