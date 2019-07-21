// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.demo.jg.FileHelper;
import ch.ethz.idsc.gokart.core.plan.TrajectoryEvents;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class TrajectoryFollowingError extends OfflineFollowingError {
  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    super.event(time, channel, byteBuffer);
    if (channel.equals(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME)) {
      List<TrajectorySample> trajectory = TrajectoryEvents.trajectory(byteBuffer);
      Tensor reference = Tensor.of(trajectory.stream().map(TrajectorySample::stateTime).map(StateTime::state));
      setReference(reference);
    }
  }

  public static void main(String[] args) throws Exception {
    Optional<File> file = FileHelper.open(args);
    if (file.isPresent()) {
      TrajectoryFollowingError followingError = new TrajectoryFollowingError();
      System.out.print("running... ");
      OfflineLogPlayer.process(file.get(), followingError);
      System.out.println("finished");
      System.out.println(followingError.getReport());
    }
  }
}
