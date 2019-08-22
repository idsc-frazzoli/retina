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
import ch.ethz.idsc.gokart.lcm.mod.Se2CurveLcm;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Nest;

/* package */ class LogFollowingError extends OfflineFollowingError {
  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    super.event(time, channel, byteBuffer);
    // trajectory following
    if (channel.equals(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME) || channel.equals(GokartLcmChannel.TRAJECTORY_XYAVT_STATETIME)) {
      List<TrajectorySample> trajectory = TrajectoryEvents.trajectory(byteBuffer);
      Tensor reference = Tensor.of(trajectory.stream().map(TrajectorySample::stateTime).map(StateTime::state));
      setReference(reference);
    } else
    // curve following
    if (channel.equals(GokartLcmChannel.PURSUIT_CURVE_SE2)) {
      Tensor reference = Se2CurveLcm.decode(byteBuffer);
      Tensor refined = Nest.of(Clothoid3.CURVE_SUBDIVISION::cyclic, reference, 5); // better error approximation
      setReference(refined);
    }
  }

  public static void main(String[] args) throws Exception {
    Optional<File> file = FileHelper.open(args);
    if (file.isPresent()) {
      LogFollowingError followingError = new LogFollowingError();
      System.out.print("running... ");
      OfflineLogPlayer.process(file.get(), followingError);
      System.out.println("finished");
      followingError.getReport().ifPresent(System.out::println);
    }
  }
}
