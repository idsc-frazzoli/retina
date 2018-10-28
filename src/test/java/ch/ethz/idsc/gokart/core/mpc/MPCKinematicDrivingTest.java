// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.owl.data.Stopwatch;
import junit.framework.TestCase;

public class MPCKinematicDrivingTest extends TestCase {
  public void testFakeData() throws Exception {
    if (MPCNative.lcmTestBinary().isPresent()) {
      Stopwatch start = Stopwatch.started();
      MPCStateEstimationProvider estimationProvider = new FakeNewsEstimator(start);
      MPCKinematicDrivingModule drivingModule = new MPCKinematicDrivingModule(estimationProvider, start, DubendorfTrack.HYPERLOOP_EIGHT);
      drivingModule.switchToTest();
      drivingModule.first();
      Thread.sleep(1000);
      System.out.println("target linmot" + drivingModule.linmotProvider.putEvent().get().target_position);
      System.out.println("target Left power" + drivingModule.rimoProvider.putEvent().get().putTireL.getTorqueRaw());
      drivingModule.last();
    }
  }
}
