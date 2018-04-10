package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;
import java.util.List;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ComputerTemperatureModule extends AbstractClockedModule {
  public static final String CHANNEL_GET = "computer.sensors.get";
  private final BinaryBlobPublisher getPublisher = new BinaryBlobPublisher(CHANNEL_GET);

  @Override
  protected void first() throws Exception {
  }

  @Override
  protected void last() {
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(5, "s");
  }

  @Override
  protected void runAlgo() {
    Components components = JSensors.get.components();
    List<Cpu> cpus = components.cpus;
    List<Gpu> gpus = components.gpus;
    int cpu_size = cpus.size();
    int gpu_size = gpus.size();
    byte[] data = new byte[20]; // TODO fix it
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.put((byte) cpu_size);
    byteBuffer.put((byte) gpu_size);
    for (final Cpu cpu : cpus) {
      // System.out.println("Found CPU component: " + cpu.name);
      if (cpu.sensors != null) {
        List<Temperature> temps = cpu.sensors.temperatures;
        byteBuffer.put((byte) temps.size());
        for (final Temperature temp : temps) {
          byteBuffer.put(temp.value.byteValue());
        }
        // Print fan speed
        // List<Fan> fans = cpu.sensors.fans;
        // for (final Fan fan : fans) {
        // System.out.println(fan.name + ": " + fan.value + " RPM");
        // }
      }
    }
    getPublisher.accept(data, byteBuffer.position());
  }
}
