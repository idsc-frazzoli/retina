// code by mh
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.util.gps.WGS84toCH1903LV03Plus;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

public class VelodyneLocalizationTable implements OfflineTableSupplier {
  // private static final String DAVIS = DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  private static final String LIDAR = VelodyneLcmChannels.pos(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private VelodynePosEvent vpe;

  public VelodyneLocalizationTable(Scalar delta) {
    this.delta = delta;
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(LIDAR) && Scalars.lessThan(time_next, time)) {
      vpe = VelodynePosEvent.vlp16(byteBuffer);
      // if (Objects.nonNull(vpe))
      // System.out.println(vpe.nmea());
      // System.out.println("export " + time.number().doubleValue());
      time_next = time.add(delta);
      Scalar degX = vpe.gpsX();
      Scalar degY = vpe.gpsY();
      Tensor metric = WGS84toCH1903LV03Plus.transform(degX, degY);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND).map(Round._6), //
          // degX.map(Magnitude.DEGREE_ANGLE).map(Round._6), //
          // degY.map(Magnitude.DEGREE_ANGLE).map(Round._6), //
          metric.map(Magnitude.METER).map(Round._2), //
          vpe.speed().map(Magnitude.VELOCITY).map(Round._3), //
          vpe.course().map(Magnitude.ONE).map(Round._6));
      System.out.println(tableBuilder.getRowCount());
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
