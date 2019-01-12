// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;

/** export davis240c imu measurements */
public class DavisImuTable implements OfflineTableSupplier {
  private static final String DAVIS = DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);

  public static DavisImuTable all() {
    return new DavisImuTable(Quantity.of(0, SI.SECOND));
  }

  // ---
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private Integer time_zero = null;

  /** @param delta use 0[s] to export every davis240c gyro record */
  public DavisImuTable(Scalar delta) {
    this.delta = delta;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(DAVIS)) {
      DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
      if (Scalars.lessThan(time_next, time)) {
        time_next = time.add(delta);
        // ---
        if (Objects.isNull(time_zero))
          time_zero = davisImuFrame.time_us_raw();
        tableBuilder.appendRow( //
            time.map(Magnitude.SECOND), // m1
            davisImuFrame.getTimeRelativeTo(time_zero).map(Magnitude.SECOND), // m2
            davisImuFrame.accelImageFrame().map(Magnitude.ACCELERATION), // m3
            davisImuFrame.temperature().map(Magnitude.DEGREE_CELSIUS), // m4
            davisImuFrame.gyroImageFrame().map(Magnitude.PER_SECOND) // m5
        );
      }
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
