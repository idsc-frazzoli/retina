// code by jph & az
package ch.ethz.idsc.gokart.core.fuse;

import java.awt.List;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ComputerSensorsEvent {
  private final int maxCpuTempCelsius;
  private final int minCpuTempCelsius;
  private Integer[] cpus_temperatures;

  public ComputerSensorsEvent(ByteBuffer byteBuffer) {
    byte protocol_id = byteBuffer.get(); // value is ignored until 2nd protocol emerges
    byte cpu_size = byteBuffer.get();
    byte gpu_size = byteBuffer.get();
    byte temps_size = byteBuffer.get();
    // TODO read temps_size x cpu size number of bytes...
    // ... and compute maximum maxCpuTempCelsius
    for (int i = 0; i < (int) cpu_size; i++) {
      for (int j = 0; j < (int) temps_size; j++) {
        cpus_temperatures[j] = (int) byteBuffer.get();
      }
    }
    maxCpuTempCelsius = Collections.max(cpus_temperatures, Comparator.nullsFirst(Comparator.naturalOrder())); // FIXME
    minCpuTempCelsius = Collections.min(cpus_temperatures, Comparator.nullsFirst(Comparator.naturalOrder())); // FIXME
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
