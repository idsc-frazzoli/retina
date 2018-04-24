// code by az and jph
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;
import java.util.List;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

// Due to the use of an external library it would be desirable to
// understand and comment more about the external library.
// The external library does not follow Java standards. therefore we may want to
// fork the library and modify the API
// ComputerSensorsModule blocks the system and jeopardises
// the communication to the micro autobox
/** the activation of the module has affected the operation of other modules in
 * a negative way. For instance, the communication with the micro autobox is impeded,
 * which results in a required manual reset. */
@Deprecated
public class ComputerSensorsModule extends AbstractClockedModule {
  public static final String CHANNEL_GET = "computer.sensors.get";
  /** a sensor readout blocks for 10[ms] on average */
  private static final Scalar PERIOD = Quantity.of(4, "s");
  // ---
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(CHANNEL_GET);

  @Override
  protected void first() throws Exception {
    // ---
  }

  @Override
  protected void last() {
    // ---
  }

  @Override
  protected Scalar getPeriod() {
    return PERIOD;
  }

  byte[] sensor() {
    Components components = JSensors.get.components();
    List<Cpu> cpus = components.cpus;
    List<Gpu> gpus = components.gpus;
    int size = 3;
    for (final Cpu cpu : cpus) {
      List<Temperature> temps = cpu.sensors.temperatures;
      size += 1 + temps.size();
    }
    byte[] data = new byte[size];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    // the current implementation may not make full use of the
    // external library and the introspection capabilities of the computer
    // therefore the first byte is reserved to encode the version of the byte ordering
    byteBuffer.put((byte) 0); // version of protocol
    byteBuffer.put((byte) cpus.size());
    byteBuffer.put((byte) gpus.size());
    for (Cpu cpu : cpus)
      if (cpu.sensors != null) {
        List<Temperature> temps = cpu.sensors.temperatures;
        byteBuffer.put((byte) temps.size());
        for (Temperature temp : temps)
          byteBuffer.put(temp.value.byteValue());
      }
    return data;
  }

  @Override
  protected void runAlgo() {
    byte[] data = sensor();
    binaryBlobPublisher.accept(data, data.length);
  }
}
