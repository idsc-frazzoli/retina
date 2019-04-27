// code by jph
package ch.ethz.idsc.retina.u3;

import java.io.File;

import ch.ethz.idsc.retina.util.sys.AppCustomization;

public class LabjackU3Config {
  public static final LabjackU3Config INSTANCE = //
      AppCustomization.load(LabjackU3Config.class, new LabjackU3Config());
  // ---
  /** not final because config file may override */
  public File directory = new File("src_c", "labjacku3");

  public File getExecutable() {
    return new File(directory, "u3adctxt");
  }

  public boolean isFeasible() {
    return getExecutable().isFile();
  }

  public static void main(String[] args) {
    AppCustomization.save(LabjackU3Config.class, new LabjackU3Config());
  }
}
