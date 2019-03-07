// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.lcm.davis.DavisDvsBlockPublisher;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;

/** export sensor data that are subject to resonance frequency of sensor rack
 * 
 * https://github.com/idsc-frazzoli/retina/files/2131149/20180624_vibration_sensor_rack.pdf */
/* package */ class SensorRackVibration implements LidarSpacialListener, DavisDvsListener, OfflineLogListener {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private static final String CHANNEL_DAVIS_IMU = //
      DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  private static final String CHANNEL_DAVIS_DVS = //
      DavisDvsBlockPublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  private static final Scalar DVS_PERIOD = Quantity.of(RationalScalar.of(1, 50), SI.SECOND);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final TableBuilder tableBuilder1 = new TableBuilder();
  private final TableBuilder tableBuilder2 = new TableBuilder();
  private final TableBuilder tableBuilder3 = new TableBuilder();
  private final TableBuilder tableBuilder4 = new TableBuilder();
  private final int[] total = new int[2];
  private Scalar time;
  private Scalar time_next = DVS_PERIOD;
  private boolean fused = false;

  public SensorRackVibration() {
    double angle_offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
    LidarSpacialProvider lidarSpacialProvider = new Vlp16SingleProvider(angle_offset, 0);
    lidarSpacialProvider.addListener(this);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    davisDvsDatagramDecoder.addDvsListener(this);
  }

  private static final int ANGLE = 9000;

  @Override // from LidarSpacialListener
  public void lidarSpacial(LidarSpacialEvent lidarSpacialEvent) {
    float[] coords = lidarSpacialEvent.coords;
    int azimuth = Math.round(coords[0]);
    if (ANGLE <= azimuth && azimuth < ANGLE + 1000) {
      if (fused) {
        fused = false;
        tableBuilder1.appendRow( //
            Magnitude.SECOND.apply(time), //
            Tensors.vector(azimuth, coords[1]));
      }
    } else
      fused = true;
  }

  Integer reference = null;

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (Objects.isNull(reference))
      reference = davisDvsEvent.time;
    ++total[davisDvsEvent.i];
    Scalar now = Quantity.of((davisDvsEvent.time - reference) * 1e-6, SI.SECOND);
    if (Scalars.lessEquals(time_next, now)) { // TODO JPH not as precise as could be
      tableBuilder4.appendRow( //
          Magnitude.SECOND.apply(time_next.subtract(DVS_PERIOD)), //
          Tensors.vectorInt(total));
      time_next = time_next.add(DVS_PERIOD);
      total[0] = 0;
      total[1] = 0;
    }
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    this.time = time;
    if (channel.equals(CHANNEL_LIDAR))
      velodyneDecoder.lasers(byteBuffer);
    else //
    if (channel.equals(CHANNEL_DAVIS_IMU)) {
      DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
      Tensor tensor = davisImuFrame.accelImageFrame();
      Scalar scalar = Magnitude.ACCELERATION.apply(tensor.Get(2));
      tableBuilder2.appendRow( //
          Magnitude.SECOND.apply(time), //
          scalar);
    } else //
    if (channel.equals(CHANNEL_DAVIS_DVS)) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    } else //
    if (channel.equals(LinmotLcmServer.CHANNEL_GET)) {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      tableBuilder3.appendRow( //
          Magnitude.SECOND.apply(time), //
          Magnitude.METER.apply(linmotGetEvent.getActualPosition()) //
      );
    }
  }

  public static void main(String[] args) throws Exception {
    SensorRackVibration vlp16Floor = new SensorRackVibration();
    GokartLogFile gokartLogFile = GokartLogFile._20180607T122743_edd2e720;
    gokartLogFile = GokartLogFile._20180607T124405_edd2e720;
    gokartLogFile = GokartLogFile._20180621T125013_2b01cac5;
    File file = DatahakiLogFileLocator.file(gokartLogFile);
    // file = UserHome.file("20180607T095321_a.lcm");
    OfflineLogPlayer.process(file, vlp16Floor);
    String name = gokartLogFile.getTitle();
    Export.of(HomeDirectory.file(name + "_lidar.csv"), vlp16Floor.tableBuilder1.toTable().map(CsvFormat.strict()));
    Export.of(HomeDirectory.file(name + "_accel.csv"), vlp16Floor.tableBuilder2.toTable().map(CsvFormat.strict()));
    Export.of(HomeDirectory.file(name + "_brake.csv"), vlp16Floor.tableBuilder3.toTable().map(CsvFormat.strict()));
    Export.of(HomeDirectory.file(name + "_event.csv"), vlp16Floor.tableBuilder4.toTable().map(CsvFormat.strict()));
  }
}
