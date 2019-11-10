// code by jph
package ch.ethz.idsc.gokart.dev.mcusb;

import java.io.File;

public enum McUsbConfig {
  INSTANCE;
  /** not final because config file may override */
  private final File directory = new File("src_c", "mcusb");

  public File getExecutableLcm() {
    return new File(directory, "DIn");
  }

  public boolean isFeasible() {
    return getExecutableLcm().isFile();
  }
}
