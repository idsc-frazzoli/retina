// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

@FunctionalInterface
public interface LogFileLocator {
  /** @param logFile
   * @return file or null if no valid file could be located
   * @throws Exception */
  File getAbsoluteFile(LogFile logFile);
}
