// code by jph
package ch.ethz.idsc.retina.u3;

import java.io.File;

import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.tensor.io.HomeDirectory;

public class LabjackU3Config {
  public static final LabjackU3Config INSTANCE = //
      AppCustomization.load(LabjackU3Config.class, new LabjackU3Config());
  // ---
  public File directory = HomeDirectory.file("Public", "exodriver", "examples", "U3");

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
