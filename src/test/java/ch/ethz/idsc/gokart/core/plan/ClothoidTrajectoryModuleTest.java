// code by jph
package ch.ethz.idsc.gokart.core.plan;

import junit.framework.TestCase;

public class ClothoidTrajectoryModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ClothoidTrajectoryModule clothoidTrajectoryModule = new ClothoidTrajectoryModule();
    GokartTrajectoryModuleTest.testSimple(clothoidTrajectoryModule);
  }

  public void testPose() throws Exception {
    TrajectoryConfig trajectoryConfig = new TrajectoryConfig();
    // TODO JPH/GJOEL add separate test that uses sightlines mapping
    trajectoryConfig.mapSightLines = false;
    ClothoidTrajectoryModule clothoidTrajectoryModule = new ClothoidTrajectoryModule(trajectoryConfig);
    GokartTrajectoryModuleTest.testPose(clothoidTrajectoryModule);
  }

  public void testFlows() {
    ClothoidTrajectoryModule clothoidTrajectoryModule = new ClothoidTrajectoryModule();
    GokartTrajectoryModuleTest.testFlows(clothoidTrajectoryModule);
  }
}
