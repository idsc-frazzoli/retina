package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import junit.framework.TestCase;

public class MPCKinematicDrivingTest extends TestCase {
  public void testFakeData() throws Exception {
    Stopwatch start = Stopwatch.started();
    MPCStateEstimationProvider estimationProvider = new fakeNewsEstimator(start);
    MPCKinematicDrivingModule drivingModule = new MPCKinematicDrivingModule(estimationProvider, start);
    drivingModule.switchToTest();
    drivingModule.first();
    Thread.sleep(1000);
    System.out.println("target linmot"+drivingModule.linmotProvider.putEvent().get().target_position);
    System.out.println("target Left power"+drivingModule.rimoProvider.putEvent().get().putTireL.getTorqueRaw());
    drivingModule.last();
  }
}
