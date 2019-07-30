// code by jph
package ch.ethz.idsc.gokart.core.plan;

import junit.framework.TestCase;

public class PureTrajectoryModuleTest extends TestCase {
    public void testSimple() throws Exception {
    PureTrajectoryModule pureTrajectoryModule = new PureTrajectoryModule();
    GokartTrajectoryModuleTest.testSimple(pureTrajectoryModule);
  }

  public void testPose() throws Exception {
    TrajectoryConfig trajectoryConfig = new TrajectoryConfig();
    // TODO JPH/GJOEL add separate test that uses sightlines mapping
    trajectoryConfig.mapSightLines = false;
    PureTrajectoryModule pureTrajectoryModule = new PureTrajectoryModule(trajectoryConfig);
    GokartTrajectoryModuleTest.testPose(pureTrajectoryModule);
  }

  public void testFlows() {
    PureTrajectoryModule pureTrajectoryModule = new PureTrajectoryModule();
    GokartTrajectoryModuleTest.testFlows(pureTrajectoryModule);
  }
}
