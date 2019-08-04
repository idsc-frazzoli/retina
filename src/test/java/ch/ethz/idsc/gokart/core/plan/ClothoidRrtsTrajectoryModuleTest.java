// code by jph
package ch.ethz.idsc.gokart.core.plan;

import junit.framework.TestCase;

public class ClothoidRrtsTrajectoryModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ClothoidRrtsTrajectoryModule clothoidTrajectoryModule = new ClothoidRrtsTrajectoryModule();
    GokartTrajectoryModuleTest.testSimple(clothoidTrajectoryModule);
  }

  public void testPose() throws Exception {
    TrajectoryConfig trajectoryConfig = new TrajectoryConfig();
    // TODO JPH/GJOEL add separate test that uses sightlines mapping
    trajectoryConfig.mapSightLines = false;
    ClothoidRrtsTrajectoryModule clothoidTrajectoryModule = new ClothoidRrtsTrajectoryModule(trajectoryConfig);
    // FIXME GJOEL MERGING ISSUE TEST
    // GokartTrajectoryModuleTest.testPose(clothoidTrajectoryModule);
  }
}
