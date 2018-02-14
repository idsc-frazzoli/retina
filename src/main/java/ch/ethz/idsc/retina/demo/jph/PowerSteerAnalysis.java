// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.lcm.autobox.MiscLcmServer;
import ch.ethz.idsc.retina.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

class PowerSteerAnalysis implements OfflineTableSupplier {
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private SteerGetEvent sge;
  private SteerPutEvent spe;
  private MiscGetEvent mge;
  TensorBuilder tensorBuilder = new TensorBuilder();

  public PowerSteerAnalysis(Scalar delta) {
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
        tensorBuilder.flatten( //
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
    return tensorBuilder.getTensor();
  }

  public static void main(String[] args) throws IOException {
    OfflineProcessing.handle(() -> new PowerSteerAnalysis(Quantity.of(0.1, SI.SECOND)));
  }
}
