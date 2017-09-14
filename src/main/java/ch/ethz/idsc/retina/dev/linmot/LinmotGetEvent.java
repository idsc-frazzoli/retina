// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.nio.ByteBuffer;

public class LinmotGetEvent {
  /** 16 bytes */
  public static final int LENGTH = 16;
  // ---
  public final short status_word;
  public final short state_variable;
  public final int actual_position;
  public final int demand_position;
  public final short winding_temp1;
  public final short winding_temp2;

  public LinmotGetEvent(ByteBuffer byteBuffer) {
    status_word = byteBuffer.getShort();
    state_variable = byteBuffer.getShort();
    actual_position = byteBuffer.getInt();
    demand_position = byteBuffer.getInt();
    winding_temp1 = byteBuffer.getShort();
    winding_temp2 = byteBuffer.getShort();
  }

  public String toInfoString() {
    return String.format("%d %d %d %d %d %d", //
        status_word, state_variable, //
        actual_position, demand_position, //
        winding_temp1, winding_temp2);
  }
}
