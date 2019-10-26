// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.slam.LidarGyroLocalization;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ class PoseLidarDelta implements OfflineTableSupplier, LidarRayBlockListener {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final TableBuilder tableBuilder = new TableBuilder();
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
  private final LidarSpacialProvider lidarSpacialProvider = LocalizationConfig.GLOBAL.planarEmulatorVlp16();
  private final LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
  private final LidarGyroLocalization lidarGyroLocalization = LidarGyroLocalization.of(LocalizationConfig.GLOBAL);
  // ---
  private Scalar time;
  private GokartPoseEvent gokartPoseEvent = null;

  public PoseLidarDelta() {
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    lidarAngularFiringCollector.addListener(this);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      this.time = time;
      gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
    } else //
    if (channel.equals(CHANNEL_LIDAR)) {
      velodyneDecoder.lasers(byteBuffer);
    }
  }

  @Override // from LidarRayBlockListener
  public synchronized void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) { // receive 2D block event
    if (Objects.nonNull(gokartPoseEvent)) {
      FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
      Tensor points = Tensors.vector(i -> Tensors.of( //
          DoubleScalar.of(floatBuffer.get()), //
          DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
      Optional<GokartPoseEvent> optional = lidarGyroLocalization.handle( //
          gokartPoseEvent.getPose(), //
          gokartPoseEvent.getVelocity(), points);
      if (optional.isPresent()) {
        GokartPoseEvent lidarPoseEvent = optional.get(); // V1
        // lidarPoseEvent = lidarGyroLocalization.handle( //
        // lidarPoseEvent.getPose(), //
        // gokartPoseEvent.getVelocity(), points).get();
        tableBuilder.appendRow( //
            Magnitude.SECOND.apply(time), //
            PoseHelper.toUnitless(gokartPoseEvent.getPose()), //
            gokartPoseEvent.getQuality(), //
            PoseHelper.toUnitless(lidarPoseEvent.getPose()), //
            lidarPoseEvent.getQuality());
      } else {
        System.err.println("miss");
      }
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.getTable();
  }

  public static void main(String[] args) throws IOException {
    String name = "20190701T175650_01";
    name = "20190701T170957_04";
    File file = new File("/media/datahaki/data/gokart/0701hum/" + name + "/log.lcm");
    PoseLidarDelta poseLidarDelta = new PoseLidarDelta();
    OfflineLogPlayer.process(file, poseLidarDelta);
    Export.of(HomeDirectory.Documents("lidarpose", name + ".csv.gz"), poseLidarDelta.getTable().map(CsvFormat.strict()));
  }
}
