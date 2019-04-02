// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationCore;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.TableBuilder;

/** class is not tested */
/* package */ class LidarLocalizationTable implements OfflineLogListener {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final TableBuilder tableBuilderOdometry = new TableBuilder();
  private final LidarLocalizationCore lidarLocalizationCore;

  public LidarLocalizationTable(LidarLocalizationCore lidarLocalizationCore) {
    this.lidarLocalizationCore = lidarLocalizationCore;
  }

  @Override
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

  public static void main(String[] args) throws IOException {
    File root = new File("/media/datahaki/data/gokart/cuts/20190311");
    File dest = new File("/media/datahaki/data/gokart/localization/20190311");
    dest.mkdir();
    List<File> list = Stream.of(root.listFiles()) //
        .filter(File::isDirectory) //
        .sorted() //
        .skip(2) //
        .limit(1) //
        .collect(Collectors.toList());
    for (File folder : list) {
      System.out.println(folder.getName());
      GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder, "log.lcm");
      LidarLocalizationOffline lidarLocalizationOffline = new LidarLocalizationOffline(gokartLogInterface.pose());
      lidarLocalizationOffline.gokartPoseListeners.add(new GokartPoseListener() {
        @Override
        public void getEvent(GokartPoseEvent gokartPoseEvent) {
          System.out.println(gokartPoseEvent.getQuality());
        }
      });
      LidarLocalizationTable lidarLocalizationTable = //
          new LidarLocalizationTable(lidarLocalizationOffline.lidarLocalizationCore());
      OfflineLogPlayer.process(gokartLogInterface.file(), lidarLocalizationOffline);
      Export.of(new File(dest, folder.getName() + ".csv.gz"), lidarLocalizationTable.tableBuilder.toTable().map(CsvFormat.strict()));
      Export.of(new File(dest, folder.getName() + "_odom.csv.gz"), lidarLocalizationTable.tableBuilderOdometry.toTable().map(CsvFormat.strict()));
    }
  }
}
