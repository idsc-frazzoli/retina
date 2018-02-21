// code by jph
package ch.ethz.idsc.demo;

import java.io.File;

public interface LogFileInterface {
  File file(File directory);

  String title();
}
