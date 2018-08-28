// code by jph
package ch.ethz.idsc.demo;

import java.io.File;

/* package */ enum StaticHelper {
  ;
  private static final String SUFFIX = ".lcm.00";

  static boolean hasLcmExtension(File file) {
    return file.getName().endsWith(SUFFIX);
  }
}
