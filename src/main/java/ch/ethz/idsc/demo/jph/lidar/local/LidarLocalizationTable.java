// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationCore;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.TableBuilder;

/** class is not tested */
/* package */ class LidarLocalizationTable implements OfflineLogListener {
  final TableBuilder tableBuilder = new TableBuilder();
  final TableBuilder tableBuilderOdometry = new TableBuilder();
  private final LidarLocalizationCore lidarLocalizationCore;

  public LidarLocalizationTable(LidarLocalizationCore lidarLocalizationCore) {
    this.lidarLocalizationCore = lidarLocalizationCore;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(Vmu931ImuChannel.INSTANCE.channel()))
      tableBuilderOdometry.appendRow( //
          Magnitude.SECOND.apply(time), //
          GokartPoseHelper.toUnitless(lidarLocalizationCore.getPose()), //
          lidarLocalizationCore.getVelocityXY().map(Magnitude.VELOCITY), //
          lidarLocalizationCore.getGyroZ().map(Magnitude.PER_SECOND), //
          lidarLocalizationCore.getGyroZ_vmu931().map(Magnitude.PER_SECOND) //
      );
    else //
    if (channel.equals(GokartPoseChannel.INSTANCE.channel())) {
      GokartPoseEvent gokartPoseEvent = lidarLocalizationCore.createPoseEvent();
      tableBuilder.appendRow( //
          Magnitude.SECOND.apply(time), //
          gokartPoseEvent.asVector());
    }
  }
}
