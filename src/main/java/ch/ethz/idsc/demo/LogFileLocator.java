// code by jph
package ch.ethz.idsc.demo;

import java.io.File;

public interface LogFileLocator {
  /** @param logFile
   * @return
   * @throws Exception */
  File getAbsoluteFile(LogFile logFile);
}
