// code by mg, jph
package ch.ethz.idsc.demo.mg.blobtrack.eval;

import java.io.File;

enum StaticHelper {
  ;
  static File warningIfNotDirectory(File directory) {
    directory.mkdir();
    if (!directory.isDirectory())
      new RuntimeException("no directory: " + directory).printStackTrace();
    return directory;
  }
}
