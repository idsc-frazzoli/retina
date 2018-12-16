// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.nio.ByteBuffer;
import java.util.Set;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

/** information received from micro-autobox about linear motor that controls the
 * break of the gokart
 * 
 * LONGTERM NRJ cite source for temperature range and other magic const */
public class LinmotGetEvent extends DataEvent {
  /** 16 bytes */
  /* package */ static final int LENGTH = 16;
  /** conversion factor 0.1 taken from data sheet */
  private static final double TO_DEGREE_CELSIUS = 0.1;
  /** actual position of 100000 corresponds to 1 cm
   * demand position uses the same scale */
  private static final double GET_POSITION_TO_METER = 1e-7;
  // ---
  public final short status_word;
  public final short state_variable;
  /** -50000 for non-braking
   * -500000 for maximum braking */
  public final int actual_position;
  /** -50000 for non-braking
   * -500000 for maximum braking
   * 
   * demand_position is on the same scale as actual_position */
  public final int demand_position;
  private final short winding_temp1;
  private final short winding_temp2;

  /** @param byteBuffer from which constructor reads 16 bytes */
  public LinmotGetEvent(ByteBuffer byteBuffer) {
    status_word = byteBuffer.getShort();
    state_variable = byteBuffer.getShort();
    actual_position = byteBuffer.getInt();
    demand_position = byteBuffer.getInt();
    winding_temp1 = byteBuffer.getShort();
    winding_temp2 = byteBuffer.getShort();
  }

  /** @return temperature of winding 1 in degree Celsius with unit "degC" */
  public Scalar getWindingTemperature1() {
    return Quantity.of(winding_temp1 * TO_DEGREE_CELSIUS, NonSI.DEGREE_CELSIUS);
  }

  /** @return temperature of winding 2 in degree Celsius with unit "degC" */
  public Scalar getWindingTemperature2() {
    return Quantity.of(winding_temp2 * TO_DEGREE_CELSIUS, NonSI.DEGREE_CELSIUS);
  }

  public Scalar getWindingTemperatureMax() {
    return Max.of( //
        getWindingTemperature1(), //
        getWindingTemperature2());
  }

  @Override // from DataEvent
  protected void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(status_word);
    byteBuffer.putShort(state_variable);
    byteBuffer.putInt(actual_position);
    byteBuffer.putInt(demand_position);
    byteBuffer.putShort(winding_temp1);
    byteBuffer.putShort(winding_temp2);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  public Scalar getActualPosition() {
    return Quantity.of(actual_position * GET_POSITION_TO_METER, SI.METER);
  }

  public Scalar getDemandPosition() {
    return Quantity.of(demand_position * GET_POSITION_TO_METER, SI.METER);
  }

  /** @return demand position minus actual position */
  public int getPositionDiscrepancyRaw() {
    return demand_position - actual_position;
  }

  public Scalar getPositionDiscrepancy() {
    return Quantity.of(getPositionDiscrepancyRaw() * GET_POSITION_TO_METER, SI.METER);
  }

  public Set<LinmotStatusWordBit> getStatusWordBits() {
    return LinmotStatusWordBit.from(status_word);
  }

  public LinmotStateVariable getStateVariable() {
    return new LinmotStateVariable(state_variable);
  }

  // bits set for guaranteed operation:
  // bit 0, 1, 2, 4, 5, 11
  private static final int OPERATIONAL_MASK = 1 + 2 + 4 + 16 + 32 + 2048;

  /** the brake is considered operational if sending messages for positioning
   * cause the brake motor to move and follow the instruction
   * 
   * @return operational status of brake */
  public boolean isOperational() {
    return (status_word & OPERATIONAL_MASK) == OPERATIONAL_MASK;
  }

  public String toInfoString() {
    return String.format("%d %d %d %d %d %d", //
        status_word, state_variable, //
        actual_position, demand_position, //
        winding_temp1, winding_temp2);
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector( //
        status_word & 0xffff, //
        state_variable & 0xffff, //
        actual_position * GET_POSITION_TO_METER, //
        demand_position * GET_POSITION_TO_METER, //
        winding_temp1 * TO_DEGREE_CELSIUS, //
        winding_temp2 * TO_DEGREE_CELSIUS //
    );
  }
}
