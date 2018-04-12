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

// TODO due to the use of an external library it would be desirable to
// understand and comment more about the external library
// TODO the external library does not follow Java standards. therefore we may want to
// fork the library and modify the API
public class ComputerSensorsModule extends AbstractClockedModule {
  public static final String CHANNEL_GET = "computer.sensors.get";
  private static final Scalar PERIOD = Quantity.of(2, "s");
  // ---
  private final BinaryBlobPublisher getPublisher = new BinaryBlobPublisher(CHANNEL_GET);

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

  @Override
  protected void runAlgo() {
    // TODO not covered by tests
    // TODO write functionality that decodes the lcm message and provides functions for accessing the temperatures
    Components components = JSensors.get.components();
    List<Cpu> cpus = components.cpus;
    List<Gpu> gpus = components.gpus;
    int cpu_size = cpus.size();
    int gpu_size = gpus.size();
    byte[] data = new byte[20]; // FIXME fix it
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    // the current implementation may not make full use of the
    // external library and the introspection capabilities of the computer
    // therefore the first byte is reserved to encode the version of the byte ordering
    byteBuffer.put((byte) 0); // version of protocol
    byteBuffer.put((byte) cpu_size);
    byteBuffer.put((byte) gpu_size);
    for (final Cpu cpu : cpus) {
      if (cpu.sensors != null) {
        List<Temperature> temps = cpu.sensors.temperatures;
        byteBuffer.put((byte) temps.size());
        for (final Temperature temp : temps)
          byteBuffer.put(temp.value.byteValue());
      }
    }
    getPublisher.accept(data, byteBuffer.position());
  }
}
