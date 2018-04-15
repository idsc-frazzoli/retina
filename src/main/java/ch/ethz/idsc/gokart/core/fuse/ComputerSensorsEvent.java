// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ComputerSensorsEvent {
  private final byte maxCpuTempCelsius;

  public ComputerSensorsEvent(ByteBuffer byteBuffer) {
    byte protocol_id = byteBuffer.get(); // value is ignored until 2nd protocol emerges
    byte cpu_size = byteBuffer.get();
    byte gpu_size = byteBuffer.get();
    byte temps_size = byteBuffer.get();
    // TODO read temps_size x cpu size number of bytes...
    // ... and compute maximum maxCpuTempCelsius
    maxCpuTempCelsius = 127; // FIXME
  }

  /** @return {@link Quantity} */
  public Scalar getTemperatureMax() {
    return Quantity.of(maxCpuTempCelsius, SI.DEGREE_CELSIUS);
  }
}
