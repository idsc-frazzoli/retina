// code by jph and az
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ComputerSensorsEvent {
  private final int maxCpuTempCelsius;
  private final int minCpuTempCelsius;

  public ComputerSensorsEvent(ByteBuffer byteBuffer) {
    // byte protocol_id =
    byteBuffer.get(); // value is ignored until 2nd protocol emerges
    byte cpu_size = byteBuffer.get();
    // byte gpu_size =
    byteBuffer.get(); // zero for the gokart pc
    byte temps_size = byteBuffer.get();
    byte[] data = new byte[cpu_size * temps_size]; // allocate byte array
    byteBuffer.get(data); // read bytes from buffer into array
    maxCpuTempCelsius = IntStream.range(0, data.length).map(i -> data[i]).boxed().reduce(Math::max).get();
    minCpuTempCelsius = IntStream.range(0, data.length).map(i -> data[i]).boxed().reduce(Math::min).get();
  }

  /** @return {@link Quantity} */
  public Scalar getTemperatureMax() {
    return Quantity.of(maxCpuTempCelsius, SI.DEGREE_CELSIUS);
  }

  /** @return {@link Quantity} */
  public Scalar getTemperatureMin() {
    return Quantity.of(minCpuTempCelsius, SI.DEGREE_CELSIUS);
  }
}
