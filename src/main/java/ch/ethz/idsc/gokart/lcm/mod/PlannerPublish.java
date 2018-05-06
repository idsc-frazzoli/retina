// code by ynager
package ch.ethz.idsc.gokart.lcm.mod;

import java.util.List;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.retina.lcm.ArrayFloatBlob;
import ch.ethz.idsc.tensor.Tensor;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

/**
 * 
 */
public enum PlannerPublish {
  ;
  // TODO encoding not final: node info may be sufficient, flow not considered yet
  public static void publishTrajectory(List<TrajectorySample> trajectory) {
    {
      Tensor tensor = Tensor.of(trajectory.stream().map(ts -> ts.stateTime().joined()));
      BinaryBlob binaryBlob = ArrayFloatBlob.encode(tensor);
      LCM.getSingleton().publish(GokartLcmChannel.TRAJECTORY_STATETIME, binaryBlob);
    }
  }
}
