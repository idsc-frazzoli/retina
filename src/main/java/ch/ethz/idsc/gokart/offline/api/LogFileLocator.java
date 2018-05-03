// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

public interface LogFileLocator {
  /** @param logFile
   * @return
   * @throws Exception */
  File getAbsoluteFile(LogFile logFile);
}
