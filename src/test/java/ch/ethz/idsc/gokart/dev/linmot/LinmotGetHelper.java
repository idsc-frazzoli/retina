// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public enum LinmotGetHelper {
  ;
  /** @param t1
   * @param t2
   * @return */
  public static LinmotGetEvent createTemperature(int t1, int t2) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putShort((short) 0x4c37);
    byteBuffer.putShort((short) 0x08c1);
    byteBuffer.putInt(10_000_000);
    byteBuffer.putInt(7000);
    byteBuffer.putShort((short) t1);
    byteBuffer.putShort((short) t2);
    byteBuffer.flip();
    return LinmotSocket.INSTANCE.createGetEvent(byteBuffer);
  }

  public static LinmotGetEvent createNonOperational() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putShort((short) 0);
    byteBuffer.putShort((short) 0x08c1);
    byteBuffer.putInt(23546);
    byteBuffer.putInt(23545);
    byteBuffer.putShort((short) 123);
    byteBuffer.putShort((short) 124);
    byteBuffer.flip();
    return LinmotSocket.INSTANCE.createGetEvent(byteBuffer);
  }

  public static LinmotGetEvent createPos(int actual_position, int demand_position) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putShort((short) 0x4c37);
    byteBuffer.putShort((short) 0x08c1);
    byteBuffer.putInt(actual_position);
    byteBuffer.putInt(demand_position);
    byteBuffer.putShort((short) 700);
    byteBuffer.putShort((short) 600);
    byteBuffer.flip();
    return LinmotSocket.INSTANCE.createGetEvent(byteBuffer);
  }
}
