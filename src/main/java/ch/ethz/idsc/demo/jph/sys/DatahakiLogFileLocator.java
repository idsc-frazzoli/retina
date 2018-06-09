// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.util.Collection;

import ch.ethz.idsc.demo.GokartLogFiles;
import ch.ethz.idsc.gokart.offline.api.LogFile;
import ch.ethz.idsc.gokart.offline.api.LogFileLocator;

public enum DatahakiLogFileLocator implements LogFileLocator {
  INSTANCE;
  // ---
  private static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");
  private static final File ALT_ROOT = new File("/media/datahaki/backup/gokartlogs");

  @Override
  public File getAbsoluteFile(LogFile logFile) {
    String title = logFile.getFilename();
    String date = title.substring(0, 8);
    {
      File file = new File(new File(LOG_ROOT, date), title);
      if (file.isFile())
        return file;
    }
    {
      File file = new File(new File(ALT_ROOT, date), title);
      if (file.isFile())
        return file;
    }
    return null;
  }

  /** @param logFile
   * @return
   * @throws Exception if file cannot be located */
  public static File file(LogFile logFile) {
    return INSTANCE.getAbsoluteFile(logFile);
  }

  /** @return */
  public static Collection<LogFile> all() {
    return GokartLogFiles.all(ALT_ROOT);
  }
}
