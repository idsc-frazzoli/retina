// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationCore;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.pose.VelocityHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;

/** implementation of {@link GokartPoseListener} is not sufficient
 * because the timestamp has to be stored as well. */
/* package */ class LidarLocalizationTable implements OfflineLogListener {
  private final LidarLocalizationCore lidarLocalizationCore;
  private final TableBuilder tableBuilderPose = new TableBuilder();
  private final TableBuilder tableBuilderOdom = new TableBuilder();

  public LidarLocalizationTable(LidarLocalizationCore lidarLocalizationCore) {
    this.lidarLocalizationCore = lidarLocalizationCore;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(Vmu931ImuChannel.INSTANCE.channel()))
      tableBuilderOdom.appendRow( //
          Magnitude.SECOND.apply(time), //
          PoseHelper.toUnitless(lidarLocalizationCore.getPose()), //
          VelocityHelper.toUnitless(lidarLocalizationCore.getVelocity()), //
          lidarLocalizationCore.getGyroZ_vmu931().map(Magnitude.PER_SECOND));
    else //
    if (channel.equals(GokartPoseChannel.INSTANCE.channel())) {
      GokartPoseEvent gokartPoseEvent = lidarLocalizationCore.createPoseEvent();
      tableBuilderPose.appendRow( //
          Magnitude.SECOND.apply(time), //
          gokartPoseEvent.asVector(), //
          GokartPoseEvent.of(byteBuffer).asVector());
    }
  }

  public Tensor tablePose() {
    return tableBuilderPose.getTable();
  }

  public Tensor tableOdom() {
    return tableBuilderOdom.getTable();
  }
}
