// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Clip;

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
  private static final Unit CELSIUS = Unit.of("degC");
  /** degree celsius */
  public static final Clip TEMPERATURE_RANGE = Clip.function( //
      Quantity.of(2, CELSIUS), Quantity.of(110, CELSIUS));
  /** bounds established using experimentation */
  public static final Clip NOMINAL_POSITION_DELTA = Clip.function(-20000, 20000);
  // ---
  public final short status_word;
  public final short state_variable;
  // negative values correspond to a pushed brake
  public final int actual_position;
  // same unit as actual_position
  public final int demand_position;
  private final short winding_temp1;
  private final short winding_temp2;

  public LinmotGetEvent(ByteBuffer byteBuffer) {
    status_word = byteBuffer.getShort();
    state_variable = byteBuffer.getShort();
    actual_position = byteBuffer.getInt();
    demand_position = byteBuffer.getInt();
    winding_temp1 = byteBuffer.getShort();
    winding_temp2 = byteBuffer.getShort();
  }

  /** @return temperature of winding 1 in degree Celsius */
  public Scalar getWindingTemperature1() {
    return Quantity.of(winding_temp1 * TO_DEGREE_CELSIUS, CELSIUS);
  }

  public boolean isSafeWindingTemperature1() {
    return TEMPERATURE_RANGE.isInside(getWindingTemperature1());
  }

  /** @return temperature of winding 2 in degree Celsius */
  public Scalar getWindingTemperature2() {
    return Quantity.of(winding_temp2 * TO_DEGREE_CELSIUS, CELSIUS);
  }

  public boolean isSafeWindingTemperature2() {
    return TEMPERATURE_RANGE.isInside(getWindingTemperature2());
  }

  public Scalar getWindingTemperatureMax() {
    return Max.of( //
        getWindingTemperature1(), //
        getWindingTemperature2());
  }

  public String toInfoString() {
    return String.format("%d %d %d %d %d %d", //
        status_word, state_variable, //
        actual_position, demand_position, //
        winding_temp1, winding_temp2);
  }

  @Override
  protected void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(status_word);
    byteBuffer.putShort(state_variable);
    byteBuffer.putInt(actual_position);
    byteBuffer.putInt(demand_position);
    byteBuffer.putShort(winding_temp1);
    byteBuffer.putShort(winding_temp2);
  }

  @Override
  protected int length() {
    return LENGTH;
  }

  public Scalar getActualPosition() {
    return Quantity.of(actual_position * GET_POSITION_TO_METER, "m");
  }

  public int getPositionDiscrepancyRaw() {
    return demand_position - actual_position;
  }

  // bits set for guaranteed operation:
  // bit 0, 1, 2, 4, 5, 11
  private static final int OPERATIONAL_MASK = 1 + 2 + 4 + 16 + 32 + 2048;

  public boolean isOperational() {
    // TODO NRJ this check is too strict
    return (status_word & OPERATIONAL_MASK) == OPERATIONAL_MASK;
  }
}
