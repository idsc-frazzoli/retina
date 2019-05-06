// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import java.io.File;
import java.util.Objects;

import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** launch and stop process for
 * Labjack U3 ADC readout and lcm publishing on channel "labjack.u3.adc" */
public final class LabjackU3Module extends AbstractModule {
  private Process process;

  @Override
  protected void first() {
    File executable = LabjackU3Config.INSTANCE.getExecutableLcm();
    ProcessBuilder processBuilder = new ProcessBuilder(executable.toString());
    try {
      process = processBuilder.start();
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new RuntimeException();
    }
  }

  @Override
  protected void last() {
    if (Objects.nonNull(process))
      process.destroy();
  }
}
