// code by az
package ch.ethz.idsc.demo.az;

import java.util.List;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import ch.ethz.idsc.gokart.core.fuse.ComputerSensorsModule;

/** code in CpuGpuTemperature evaluates the 3rd party libary
 * com.profesorfalken.jsensors
 * 
 * the api is now used in {@link ComputerSensorsModule} */
enum CpuGpuTemperature {
  ;
  public static void main(String[] args) {
    Components components = JSensors.get.components();
    List<Cpu> cpus = components.cpus;
    List<Gpu> gpus = components.gpus;
    System.out.println("Cannot detect gpu:" + gpus.isEmpty());
    if (cpus != null) {
      for (final Cpu cpu : cpus) {
        System.out.println("Found CPU component: " + cpu.name);
        if (cpu.sensors != null) {
          System.out.println("Sensors: ");
          // Print temperatures
          List<Temperature> temps = cpu.sensors.temperatures;
          for (final Temperature temp : temps) {
            System.out.println(temp.name + ": " + temp.value + " C");
          }
          // Print fan speed
          List<Fan> fans = cpu.sensors.fans;
          for (final Fan fan : fans) {
            System.out.println(fan.name + ": " + fan.value + " RPM");
          }
        }
      }
      for (final Gpu gpu : gpus) {
        System.out.println("Found GPU component: " + gpu.name);
        if (gpu.sensors != null) {
          System.out.println("Sensors: ");
          // Print temperatures
          List<Temperature> temps = gpu.sensors.temperatures;
          for (final Temperature temp : temps) {
            System.out.println(temp.name + ": " + temp.value + " C");
          }
          // Print fan speed
          List<Fan> fans = gpu.sensors.fans;
          for (final Fan fan : fans) {
            System.out.println(fan.name + ": " + fan.value + " RPM");
          }
        }
      }
    }
  }
}
