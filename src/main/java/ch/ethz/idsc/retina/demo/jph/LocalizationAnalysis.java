// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.retina.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.util.gps.WGS84toCH1903LV03Plus;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;

class LocalizationAnalysis implements OfflineTableSupplier {
  private static final String DAVIS = DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  private static final String LIDAR = VelodyneLcmChannels.pos(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private RimoGetEvent rge;
  private RimoPutEvent rpe;
  private VelodynePosEvent vpe;
  private DavisImuFrame dif;
  GokartPoseEvent gpe;
  final TensorBuilder tensorBuilder = new TensorBuilder();

  public LocalizationAnalysis(Scalar delta) {
    this.delta = delta;
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      rge = new RimoGetEvent(byteBuffer);
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_PUT)) {
      rpe = RimoPutHelper.from(byteBuffer);
    } else //
    if (channel.equals(LIDAR)) {
      vpe = VelodynePosEvent.vlp16(byteBuffer);
      System.out.println(vpe.nmea());
    } else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gpe = new GokartPoseEvent(byteBuffer);
    } else //
    if (channel.equals(DAVIS)) {
      dif = new DavisImuFrame(byteBuffer);
    }
    if (Scalars.lessThan(time_next, time)) {
      if (Objects.nonNull(rge) && Objects.nonNull(rpe) && Objects.nonNull(vpe) && //
          Objects.nonNull(gpe) && Objects.nonNull(dif)) {
        // System.out.println("export " + time.number().doubleValue());
        time_next = time.add(delta);
        Tensor rates = rge.getAngularRate_Y_pair();
        Scalar speed = Mean.of(rates).multiply(ChassisGeometry.GLOBAL.tireRadiusRear).Get();
        Scalar rate = Differences.of(rates).Get(0) //
            .multiply(RationalScalar.HALF) //
            .multiply(ChassisGeometry.GLOBAL.tireRadiusRear) //
            .divide(ChassisGeometry.GLOBAL.yTireRear);
        Tensor pose = gpe.getPose().extract(0, 2).map(Magnitude.METER);
        Scalar degX = vpe.gpsX();
        Scalar degY = vpe.gpsY();
        Tensor metric = WGS84toCH1903LV03Plus.transform(degX, degY);
        tensorBuilder.flatten( //
            time.map(Magnitude.SECOND), //
            speed.map(Magnitude.VELOCITY), //
            rate.map(Magnitude.ANGULAR_RATE), //
            gpe.getQuality(), //
            pose, //
            gpe.getPose().Get(2).map(Magnitude.ONE), //
            metric.map(Magnitude.METER), //
            dif.gyroImageFrame().map(Magnitude.ANGULAR_RATE) //
        );
      }
    }
  }

  @Override
  public Tensor getTable() {
    return tensorBuilder.getTensor();
  }

  public static void main(String[] args) throws IOException {
    OfflineProcessing.INSTANCE.handle(() -> new LocalizationAnalysis(Quantity.of(0.05, SI.SECOND)));
  }
}
