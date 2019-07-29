// code by mg, jph
package ch.ethz.idsc.retina.app.blob.eval;

import java.io.File;

/* package */ enum StaticHelper {
  ;
  static File warningIfNotDirectory(File directory) {
    directory.mkdir();
    if (!directory.isDirectory())
      new RuntimeException("no directory: " + directory).printStackTrace();
    return directory;
  }
}
