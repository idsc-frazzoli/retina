// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.nio.ByteBuffer;
import java.util.List;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.mod.PlannerPublish;
import ch.ethz.idsc.owl.gui.ani.TrajectoryListener;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.retina.lcm.ArrayFloatBlob;
import ch.ethz.idsc.retina.lcm.SimpleLcmClient;
import ch.ethz.idsc.tensor.Tensor;

public class TrajectoryLcmClient extends SimpleLcmClient<TrajectoryListener> {
  public static TrajectoryLcmClient xyat() {
    return new TrajectoryLcmClient(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME);
  }

  public static TrajectoryLcmClient xyavt() {
    return new TrajectoryLcmClient(GokartLcmChannel.TRAJECTORY_XYAVT_STATETIME);
  }

  // ---
  private final String channel;

  private TrajectoryLcmClient(String channel) {
    this.channel = channel;
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    Tensor tensor = ArrayFloatBlob.decode(byteBuffer);
    List<TrajectorySample> trajectory = PlannerPublish.getTrajectory(tensor);
    listeners.forEach(listener -> listener.trajectory(trajectory));
  }

  @Override // from BinaryLcmClient
  protected String channel() {
    return channel;
  }
}
