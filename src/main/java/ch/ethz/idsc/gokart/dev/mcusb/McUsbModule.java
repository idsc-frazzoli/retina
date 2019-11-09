// code by jph
package ch.ethz.idsc.gokart.dev.mcusb;

import java.io.File;
import java.util.Objects;

import ch.ethz.idsc.retina.util.sys.AbstractModule;

/**  */
public final class McUsbModule extends AbstractModule {
  private Process process;

  @Override
  protected void first() {
    File executable = McUsbConfig.INSTANCE.getExecutableLcm();
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
