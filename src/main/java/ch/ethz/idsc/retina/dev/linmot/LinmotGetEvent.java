// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;

/** information received from micro-autobox about linear motor that controls the
 * break of the gokart */
public class LinmotGetEvent extends DataEvent {
  /** 16 bytes */
  public static final int LENGTH = 16;
  // TODO NRJ document conversion factor
  private static final double TO_DEGREE_CELSIUS = 0.1;
  // ---
  public final short status_word;
  public final short state_variable;
  public final int actual_position;
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
  public double windingTemperature1() {
    return winding_temp1 * TO_DEGREE_CELSIUS;
  }

  /** @return temperature of winding 2 in degree Celsius */
  public double windingTemperature2() {
    return winding_temp2 * TO_DEGREE_CELSIUS;
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
}
