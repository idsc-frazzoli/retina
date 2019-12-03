// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.io.Timing;
import junit.framework.TestCase;

public class MPCDrivingAbstractModuleTest extends TestCase {
  public void testFakeData() throws Exception {
    if (MPCNative.lcmTestBinary().isPresent()) {
      Timing timing = Timing.started();
      MPCStateEstimationProvider mpcStateEstimationProvider = new FakeNewsEstimator(timing);
      MPCDrivingAbstractModule mpcAbstractDrivingModule = new MPCDrivingKinematicModule(mpcStateEstimationProvider, timing, DubendorfTrack.HYPERLOOP_EIGHT);
      mpcAbstractDrivingModule.first();
      Thread.sleep(3000);
      System.out.println("target linmot" + mpcAbstractDrivingModule.mpcLinmotProvider.putEvent().get().target_position);
      System.out.println("target Left power" + mpcAbstractDrivingModule.mpcRimoProvider.putEvent().get().putTireL.getTorqueRaw());
      mpcAbstractDrivingModule.last();
    }
  }
}
