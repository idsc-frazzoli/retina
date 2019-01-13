// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.util.gps.WGS84toCH1903LV03Plus;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

public class LocalizationTable implements OfflineTableSupplier {
  private static final String DAVIS = DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  private static final String LIDAR = VelodyneLcmChannels.pos(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Scalar delta;
  private final boolean usePose;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private RimoGetEvent rge;
  private RimoPutEvent rpe;
  private VelodynePosEvent vpe;
  private DavisImuFrame dif;
  private GokartPoseEvent gpe;

  public LocalizationTable(Scalar delta, boolean usePose) {
    this.delta = delta;
    this.usePose = usePose;
    if (!usePose)
      System.err.println("warning: pose is not exported");
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
      // System.out.println(vpe.nmea());
    } else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gpe = new GokartPoseEvent(byteBuffer);
    } else //
    if (channel.equals(DAVIS)) {
      dif = new DavisImuFrame(byteBuffer);
    }
    if (Scalars.lessThan(time_next, time)) {
      // if (Objects.nonNull(vpe))
      // System.out.println(vpe.nmea());
      if (Objects.nonNull(rge) && Objects.nonNull(rpe) && Objects.nonNull(vpe) && //
          (Objects.nonNull(gpe) || !usePose) && //
          Objects.nonNull(dif) && vpe.isValid()) {
        // System.out.println("export " + time.number().doubleValue());
        time_next = time.add(delta);
        Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rge);
        Scalar rate = ChassisGeometry.GLOBAL.odometryTurningRate(rge);
        Scalar degX = vpe.gpsX();
        Scalar degY = vpe.gpsY();
        Tensor metric = WGS84toCH1903LV03Plus.transform(degX, degY);
        tableBuilder.appendRow( //
            time.map(Magnitude.SECOND).map(Round._6), //
            speed.map(Magnitude.VELOCITY).map(Round._3), //
            rate.map(Magnitude.PER_SECOND).map(Round._3), //
            getPose(), //
            degX.map(Magnitude.DEGREE_ANGLE).map(Round._6), //
            degY.map(Magnitude.DEGREE_ANGLE).map(Round._6), //
            metric.map(Magnitude.METER).map(Round._2), //
            vpe.speed().map(Magnitude.VELOCITY).map(Round._3), //
            vpe.course().map(Magnitude.ONE).map(Round._6), //
            dif.gyroImageFrame().map(Magnitude.PER_SECOND).map(Round._5) //
        );
        System.out.println(tableBuilder.getRowCount());
      }
    }
  }

  private Tensor getPose() {
    if (usePose) {
      Tensor pose = gpe.getPose().extract(0, 2).map(Magnitude.METER);
      return Tensors.of( //
          gpe.getQuality().map(Round._3), // 1
          pose.map(Round._3), // 2
          gpe.getPose().Get(2).map(Magnitude.ONE).map(Round._5)); // 1
    }
    return Array.zeros(4);
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
