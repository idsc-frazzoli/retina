// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.dev.misc.MiscGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.lcm.autobox.MiscLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;

/** creates table related to steering measurements and commands to steering unit
 * 
 * the columns in the table have the following ordering
 * <pre>
 * timestamp in seconds
 * motAsp_CANInput
 * motAsp_Qual
 * tsuTrq_CANInput
 * tsuTrq_Qual
 * refMotTrq_CANInput
 * estMotTrq_CANInput
 * estMotTrq_Qual
 * gcpRelRckPos
 * gcpRelRckQual
 * gearRat
 * halfRckPos
 * command
 * torque
 * steeringBatteryVoltage [V]
 * </pre>
 * 
 * for the meaning of variables refer to {@link SteerGetEvent}, {@link SteerPutEvent} */
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

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(SteerLcmServer.CHANNEL_GET))
      sge = new SteerGetEvent(byteBuffer);
    else //
    if (channel.equals(SteerLcmServer.CHANNEL_PUT))
      spe = SteerPutEvent.from(byteBuffer);
    else //
    if (channel.equals(MiscLcmServer.CHANNEL_GET))
      mge = new MiscGetEvent(byteBuffer);
    // ---
    if (Scalars.lessEquals(time_next, time))
      if (Objects.nonNull(sge) && Objects.nonNull(spe) && Objects.nonNull(mge)) {
        time_next = time.add(delta);
        // append a row in the table that is a concatenation of
        // [time, measurements, commands, battery-voltage]
        tableBuilder.appendRow( //
            time.map(Magnitude.SECOND), // 0
            sge.asVector(), // [1 - 11]
            spe.asVector(), // [12 - 13]
            mge.getSteerBatteryVoltage().map(Magnitude.VOLT) // [14]
        );
      }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
