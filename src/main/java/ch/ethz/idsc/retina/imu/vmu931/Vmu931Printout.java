// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

public enum Vmu931Printout implements Vmu931Listener {
  INSTANCE;
  // ---
  @Override
  public void accelerometer(ByteBuffer byteBuffer) {
    int timestamp_ms = byteBuffer.getInt();
    /** is in g
     * when sensor is flat on the table/floor
     * the xyz vector == {-0.033, 0.022, 0.951} */
    float x = byteBuffer.getFloat();
    float y = byteBuffer.getFloat();
    float z = byteBuffer.getFloat();
    System.out.println("acc =" + timestamp_ms + " " + Tensors.vector(x, y, z).map(Round._3));
  }

  @Override
  public void gyroscope(ByteBuffer byteBuffer) {
    int timestamp_ms = byteBuffer.getInt();
    /** scalar has unit [deg*s^-1] */
    float x = byteBuffer.getFloat();
    float y = byteBuffer.getFloat();
    float z = byteBuffer.getFloat();
    System.out.println("gryo=" + timestamp_ms + " " + Tensors.vector(x, y, z).map(Round._3));
  }
}
