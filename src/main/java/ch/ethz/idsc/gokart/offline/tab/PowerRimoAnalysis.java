// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;

/** creates table related to rear wheel motor measurements and commands
 * 
 * the columns in the table have the following ordering
 * <pre>
 * timestamp
 * --- from left wheel:
 * status_word
 * actual_rate * sign * MIN_TO_S
 * rms_motor_current
 * dc_bus_voltage
 * error_code
 * temperature_motor
 * temperature_heatsink
 * --- from right wheel:
 * status_word
 * actual_rate * sign * MIN_TO_S
 * rms_motor_current
 * dc_bus_voltage
 * error_code
 * temperature_motor
 * temperature_heatsink
 * torque command left wheel
 * torque command right wheel
 * </pre>
 * 
 * for more information on the variables see {@link RimoGetEvent}, and {@link RimoPutEvent} */
public class PowerRimoAnalysis implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Scalar delta;
  // ---
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private RimoGetEvent rge;
  private RimoPutEvent rpe;

  public PowerRimoAnalysis(Scalar delta) {
    this.delta = delta;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      rge = new RimoGetEvent(byteBuffer);
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_PUT)) {
      rpe = RimoPutHelper.from(byteBuffer);
    }
    if (Scalars.lessEquals(time_next, time)) {
      if (Objects.nonNull(rge) && Objects.nonNull(rpe)) {
        // System.out.println("export " + time.number().doubleValue());
        time_next = time.add(delta);
        tableBuilder.appendRow( //
            time.map(Magnitude.SECOND), //
            rge.asVector(), //
            rpe.getTorque_Y_pair().map(Magnitude.ARMS) // ARMS
        );
      }
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
