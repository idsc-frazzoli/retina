// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.lcm.autobox.MiscLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PowerSteerTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private SteerGetEvent sge;
  private SteerPutEvent spe;
  private MiscGetEvent mge;

  public PowerSteerTable(Scalar delta) {
    this.delta = delta;
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      sge = new SteerGetEvent(byteBuffer);
    } else //
    if (channel.equals(SteerLcmServer.CHANNEL_PUT)) {
      spe = SteerPutEvent.from(byteBuffer);
    } else //
    if (channel.equals(MiscLcmServer.CHANNEL_GET)) {
      mge = new MiscGetEvent(byteBuffer);
    }
    if (Scalars.lessThan(time_next, time)) {
      if (Objects.nonNull(sge) && Objects.nonNull(spe) && Objects.nonNull(mge)) {
        time_next = time.add(delta);
        tableBuilder.appendRow( //
            time.map(Magnitude.SECOND), //
            sge.values_raw(), //
            spe.values_raw(), //
            mge.getSteerBatteryVoltage().map(Magnitude.VOLT) //
        );
      }
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
