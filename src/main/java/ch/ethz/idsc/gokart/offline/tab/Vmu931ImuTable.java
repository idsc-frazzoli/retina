// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;

/** export davis240c imu measurements */
public class Vmu931ImuTable implements OfflineTableSupplier {
  private static final String VMU931 = GokartLcmChannel.VMU931_AG;

  public static Vmu931ImuTable all() {
    return new Vmu931ImuTable(Quantity.of(0, SI.SECOND));
  }

  // ---
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private Integer time_zero = null;

  /** @param delta use 0[s] to export every davis240c gyro record */
  public Vmu931ImuTable(Scalar delta) {
    this.delta = delta;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(VMU931)) {
      Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
      if (Scalars.lessThan(time_next, time)) {
        time_next = time.add(delta);
        // ---
        if (Objects.isNull(time_zero))
          time_zero = vmu931ImuFrame.timestamp_ms();
        tableBuilder.appendRow( //
            RealScalar.of(vmu931ImuFrame.timestamp_ms()), // m1
            vmu931ImuFrame.acceleration().map(Magnitude.ACCELERATION), vmu931ImuFrame.gyroscope().map(Magnitude.PER_SECOND));
      }
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
