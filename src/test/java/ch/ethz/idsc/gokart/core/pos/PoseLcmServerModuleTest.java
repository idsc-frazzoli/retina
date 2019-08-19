// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

class GokartPoseCapture implements GokartPoseListener {
  GokartPoseEvent gokartPoseEvent;

  @Override
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }
}

public class PoseLcmServerModuleTest extends TestCase {
  public void testRunAlgo() throws Exception {
    GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
    GokartPoseCapture gokartPoseCapture = new GokartPoseCapture();
    gokartPoseLcmClient.addListener(gokartPoseCapture);
    gokartPoseLcmClient.startSubscriptions();
    assertNull(gokartPoseCapture.gokartPoseEvent);
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    PoseLcmServerModule poseLcmServerModule = new PoseLcmServerModule();
    poseLcmServerModule.first();
    assertNull(gokartPoseCapture.gokartPoseEvent);
    poseLcmServerModule.runAlgo();
    Thread.sleep(20);
    assertNotNull(gokartPoseCapture.gokartPoseEvent);
    poseLcmServerModule.last();
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
    gokartPoseLcmClient.stopSubscriptions();
  }

  public void testModule() throws Exception {
    GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
    GokartPoseCapture gokartPoseCapture = new GokartPoseCapture();
    gokartPoseLcmClient.addListener(gokartPoseCapture);
    gokartPoseLcmClient.startSubscriptions();
    assertNull(gokartPoseCapture.gokartPoseEvent);
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    ModuleAuto.INSTANCE.runOne(PoseLcmServerModule.class);
    Thread.sleep(100);
    assertNotNull(gokartPoseCapture.gokartPoseEvent);
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
    ModuleAuto.INSTANCE.endOne(PoseLcmServerModule.class);
  }

  public void testRate() {
    assertEquals(Magnitude.PER_SECOND.toInt(PoseLcmServerModule.RATE), 50);
  }

  public void testPeriod() {
    PoseLcmServerModule poseLcmServerModule = new PoseLcmServerModule();
    assertEquals(Magnitude.MILLI_SECOND.toLong(poseLcmServerModule.getPeriod()), 20);
  }

  public void testChannel() {
    assertEquals(GokartLcmChannel.POSE_LIDAR, "gokart.pose.lidar");
  }
}
