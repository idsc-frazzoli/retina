// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.gokart.core.mpc.DubendorfTrack;
import ch.ethz.idsc.gokart.core.mpc.GokartState;
import ch.ethz.idsc.gokart.core.mpc.LcmMPCControlClient;
import ch.ethz.idsc.gokart.core.mpc.MPCInformationProvider;
import ch.ethz.idsc.gokart.core.mpc.MPCNative;
import ch.ethz.idsc.gokart.core.mpc.MPCOptimizationParameter;
import ch.ethz.idsc.gokart.core.mpc.MPCPathParameter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GlobalViewLcmModuleWithMPCTest extends TestCase {
  GokartState gokartState;
  
  public void testSimple() throws Exception {
    LcmMPCControlClient lcmMPCControlClient = new LcmMPCControlClient();
    GlobalViewLcmModule globalViewLcmModule = new GlobalViewLcmModule();
    lcmMPCControlClient.switchToExternalStart();
    lcmMPCControlClient.start();
    globalViewLcmModule.first();
    gokartState = new GokartState(//
        11, //
        1f, //
        0, //
        0, //
        37f, //
        44f, //
        -0.3f, //
        0, //
        0, //
        0, 60);
    MPCOptimizationParameter optimizationParameter = new MPCOptimizationParameter(Quantity.of(10, SI.VELOCITY));
    lcmMPCControlClient.publishOptimizationParameter(optimizationParameter);
    lcmMPCControlClient.registerControlUpdateLister(MPCInformationProvider.getInstance());
    DubendorfTrack track = DubendorfTrack.CHICANE;
    Tensor position = Tensors.of(gokartState.getX(), gokartState.getY());
    MPCPathParameter mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINEPREVIEWSIZE, position);
    lcmMPCControlClient.publishControlRequest(gokartState, mpcPathParameter);
    Thread.sleep(1000);

    for(int i = 0; i<100; i++)
    {
      System.out.println("send request");
      gokartState = lcmMPCControlClient.lastcns.steps[3].state;
      position = Tensors.of(gokartState.getX(), gokartState.getY());
      mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINEPREVIEWSIZE, position);
      lcmMPCControlClient.publishControlRequest(gokartState, mpcPathParameter);
      Thread.sleep(3000);
    }
    globalViewLcmModule.last();
    lcmMPCControlClient.stop();
  }
}
