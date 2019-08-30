// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.io.Timing;
import junit.framework.TestCase;

public class MPCAbstractDrivingModuleTest extends TestCase {
  public void testFakeData() throws Exception {
    if (MPCNative.lcmTestBinary().isPresent()) {
      Timing timing = Timing.started();
      MPCStateEstimationProvider estimationProvider = new FakeNewsEstimator(timing);
      MPCAbstractDrivingModule drivingModule = new MPCKinematicDrivingModule(estimationProvider, timing, DubendorfTrack.HYPERLOOP_EIGHT);
      // drivingModule.switchToTest(); // TODO manage (external) process somewhere
      drivingModule.first();
      Thread.sleep(3000);
      System.out.println("target linmot" + drivingModule.mpcLinmotProvider.putEvent().get().target_position);
      System.out.println("target Left power" + drivingModule.mpcRimoProvider.putEvent().get().putTireL.getTorqueRaw());
      drivingModule.last();
    }
  }
}
