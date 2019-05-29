// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import ch.ethz.idsc.demo.jg.FileHelper;
import ch.ethz.idsc.gokart.calib.steer.RimoTwdOdometry;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.dev.rimo.RimoConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.mod.ClothoidPlanLcm;
import ch.ethz.idsc.owl.math.planar.ClothoidTerminalRatios;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Sign;

public class ClothoidPlanAnalysis implements OfflineLogListener {
  private static final Set<String> NOTIFICATIONS = new HashSet<>(Arrays.asList( //
      GokartLcmChannel.PURSUIT_PLAN, GokartLcmChannel.POSE_LIDAR, RimoLcmServer.CHANNEL_GET));
  // ---
  private boolean isForward = true;
  private Tensor gokartPose = Tensors.empty();
  private TensorUnaryOperator followingError = null;
  // ---
  private Tensor replanningTimes = Tensors.empty();
  private Tensor estimationErrors = Tensors.empty();
  private Tensor followingErrors = Tensors.empty();

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    switch (channel) {
    case GokartLcmChannel.PURSUIT_PLAN:
      notify(time, GokartLcmChannel.PURSUIT_PLAN);
      ClothoidPlan clothoidPlan = ClothoidPlanLcm.decode(byteBuffer, isForward);
      // execution frequency
      replanningTimes.append(time);
      // test estimate
      estimationErrors.append(se2Error(clothoidPlan.curve().get(0), gokartPose));
      // test following
      Tensor refined = Nest.of(ClothoidTerminalRatios.CURVE_SUBDIVISION::string, clothoidPlan.curve(), 5);
      followingError = pose -> {
        int idx = ArgMin.of(Tensor.of(refined.stream().map(pose::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
        return se2Error(refined.get(idx), pose);
      };
      break;
    case GokartLcmChannel.POSE_LIDAR:
      notify(time, GokartLcmChannel.POSE_LIDAR);
      gokartPose = GokartPoseEvent.of(byteBuffer).getPose();
      if (Objects.nonNull(followingError))
        followingErrors.append(followingError.apply(gokartPose));
      break;
    case RimoLcmServer.CHANNEL_GET:
      notify(time, RimoLcmServer.CHANNEL_GET);
      Scalar speed = RimoConfig.GLOBAL.speedChop().apply(RimoTwdOdometry.tangentSpeed(new RimoGetEvent(byteBuffer)));
      isForward = Sign.isPositiveOrZero(speed);
      break;
    }
  }

  private void notify(Scalar time, String channel) {
    if (NOTIFICATIONS.contains(channel))
      System.out.println(N.DOUBLE.apply(time) + ":\t" + channel);
  }

  private Tensor se2Error(Tensor t1, Tensor t2) {
    Scalar positionError = Norm._2.of(Extract2D.FUNCTION.apply(t1.subtract(t2)));
    Scalar headingError = t2.Get(2).subtract(t1.Get(2));
    return Tensors.of(positionError, headingError);
  }

  public Scalar averageReplanningTime() {
    return Mean.of(Differences.of(replanningTimes)).Get();
  }

  public Tensor averageEstimationError() {
    return Mean.of(estimationErrors);
  }

  public Tensor averageFollowingError() {
    return Mean.of(followingErrors);
  }

  public String getReport() {
    Tensor estimationError = averageEstimationError();
    Tensor followingError = averageFollowingError();
    return "clothoid plan analysis" + //
        "\n\taverage time between replanning:\t" + N.DOUBLE.of(averageReplanningTime()) + //
        "\n\taverage pose estimation error:\tposition=" + estimationError.Get(0) + ",\theading=" + estimationError.Get(1) + //
        "\n\taverage following error:\tposition=" + followingError.Get(0) + ",\theading=" + followingError.Get(1);
  }

  public static void main(String[] args) throws Exception {
    Optional<File> optional = FileHelper.open(args);
    if (optional.isPresent()) {
      ClothoidPlanAnalysis analysis = new ClothoidPlanAnalysis();
      OfflineLogPlayer.process(optional.get(), analysis);
      System.out.println(analysis.getReport());
    }
  }
}
