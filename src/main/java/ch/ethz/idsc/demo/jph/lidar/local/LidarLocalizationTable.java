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
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationCore;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ class LidarLocalizationTable implements OfflineTableSupplier, LidarRayBlockListener {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final LidarLocalizationCore lidarLocalizationCore = new LidarLocalizationCore();
  private final TableBuilder tableBuilder = new TableBuilder();
  private final TableBuilder tableBuilderOdometry = new TableBuilder();

  public LidarLocalizationTable() {
    velodyneDecoder.addRayListener(lidarLocalizationCore.lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarLocalizationCore.lidarRotationProvider);
    lidarLocalizationCore.lidarAngularFiringCollector.addListener(this);
    lidarLocalizationCore.setTracking(true);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(Vmu931ImuChannel.INSTANCE.channel())) {
      lidarLocalizationCore.vmu931ImuFrame(new Vmu931ImuFrame(byteBuffer));
      tableBuilderOdometry.appendRow( //
          Magnitude.SECOND.apply(time), //
          GokartPoseHelper.toUnitless(lidarLocalizationCore.getPose()), //
          lidarLocalizationCore.getVelocityXY().map(Magnitude.VELOCITY), //
          lidarLocalizationCore.getGyroZ().map(Magnitude.PER_SECOND), //
          lidarLocalizationCore.getGyroZ_vmu931().map(Magnitude.PER_SECOND) //
      );
    } else //
    if (channel.equals(CHANNEL_LIDAR))
      velodyneDecoder.lasers(byteBuffer);
    else //
    if (channel.equals(GokartPoseChannel.INSTANCE.channel())) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      GokartPoseEvent gokartPoseUpdat = lidarLocalizationCore.createPoseEvent();
      tableBuilder.appendRow( //
          Magnitude.SECOND.apply(time), //
          gokartPoseEvent.asVector(), //
          gokartPoseUpdat.asVector());
    }
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    lidarLocalizationCore.run();
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
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
      LidarLocalizationTable lidarLocalizationTable = new LidarLocalizationTable();
      lidarLocalizationTable.lidarLocalizationCore.resetPose(gokartLogInterface.pose());
      OfflineLogPlayer.process(gokartLogInterface.file(), lidarLocalizationTable);
      Export.of(new File(dest, folder.getName() + ".csv.gz"), lidarLocalizationTable.getTable().map(CsvFormat.strict()));
      Export.of(new File(dest, folder.getName() + "_odom.csv.gz"), lidarLocalizationTable.tableBuilderOdometry.toTable().map(CsvFormat.strict()));
    }
  }
}
