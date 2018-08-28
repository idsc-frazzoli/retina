// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;

public class DavisImuTable implements OfflineTableSupplier {
  private static final String DAVIS = DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  // ---
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private RimoGetEvent rge;
  private DavisImuFrame dif;
  private Integer time_zero = null;

  /** @param delta use 0[s] to export every davis240c gyro record */
  public DavisImuTable(Scalar delta) {
    this.delta = delta;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      rge = new RimoGetEvent(byteBuffer);
    } else //
    if (channel.equals(DAVIS)) {
      dif = new DavisImuFrame(byteBuffer);
      if (Scalars.lessThan(time_next, time) && Objects.nonNull(rge)) {
        time_next = time.add(delta);
        // ---
        if (Objects.isNull(time_zero))
          time_zero = dif.time_us_raw();
        tableBuilder.appendRow( //
            time.map(Magnitude.SECOND), // m1
            dif.getTimeRelativeTo(time_zero).map(Magnitude.SECOND), // m2
            dif.accelImageFrame().map(Magnitude.ACCELERATION), // m3
            dif.temperature().map(Magnitude.DEGREE_CELSIUS), // m4
            dif.gyroImageFrame().map(Magnitude.PER_SECOND), // m5
            rge.getAngularRate_Y_pair().map(Magnitude.PER_SECOND) // m6,7
        );
      }
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
