// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.nio.ByteBuffer;
import java.util.List;

import ch.ethz.idsc.gokart.core.pure.TrajectoryEvents;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class TrajectoryFollowingError extends OfflineFollowingError{
  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    super.event(time, channel, byteBuffer);
    if (channel.equals(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME)) {
      List<TrajectorySample> trajectory = TrajectoryEvents.trajectory(byteBuffer);
      Tensor reference = Tensor.of(trajectory.stream().map(TrajectorySample::stateTime).map(StateTime::state));
      setReference(reference);
    }
  }
}
