// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import java.io.File;

public enum LabjackU3Config {
  INSTANCE;
  /** not final because config file may override */
  private final File directory = new File("src_c", "labjacku3");

  public File getExecutableLcm() {
    return new File(directory, "u3adclcm");
  }

  public boolean isFeasible() {
    return getExecutableLcm().isFile();
  }

  public File getExecutableTxt() {
    return new File(directory, "u3adctxt");
  }
}
