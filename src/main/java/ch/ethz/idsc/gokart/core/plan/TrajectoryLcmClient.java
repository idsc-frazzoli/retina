// code by jph
package ch.ethz.idsc.gokart.core.plan;

import java.nio.ByteBuffer;
import java.util.List;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.owl.ani.api.TrajectoryListener;
import ch.ethz.idsc.owl.math.state.TrajectorySample;

public class TrajectoryLcmClient extends SimpleLcmClient<TrajectoryListener> {
  public static TrajectoryLcmClient xyat() {
    return new TrajectoryLcmClient(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME);
  }

  public static TrajectoryLcmClient xyavt() {
    return new TrajectoryLcmClient(GokartLcmChannel.TRAJECTORY_XYAVT_STATETIME);
  }

  // ---
  private TrajectoryLcmClient(String channel) {
    super(channel);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    List<TrajectorySample> trajectory = TrajectoryEvents.trajectory(byteBuffer);
    listeners.forEach(listener -> listener.trajectory(trajectory));
  }
}
