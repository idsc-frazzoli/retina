// code by jph
package ch.ethz.idsc.demo.gz;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.demo.GokartLogFiles;
import ch.ethz.idsc.gokart.offline.api.LogFile;
import ch.ethz.idsc.gokart.offline.api.LogFileLocator;

/* package */ enum GioeleLogFileLocator implements LogFileLocator {
  INSTANCE;
  // ---
  /** the archive of all log files is kept on an external hard-drive */
  private static final File ARCHIVE = new File("/home/gio/gokartlogs");
  private static final List<File> LOG_ROOT = Arrays.asList(ARCHIVE);

  @Override
  public File getAbsoluteFile(LogFile logFile) {
    String title = logFile.getFilename();
    for (File dir : LOG_ROOT) {
      File file = new File(dir, title);
      if (file.isFile())
        return file;
    }
    return null;
  }

  /** @param logFile
   * @return
   * @throws Exception
   * if file cannot be located */
  public static File file(LogFile logFile) {
    return INSTANCE.getAbsoluteFile(logFile);
  }

  /** @return */
  public static Collection<LogFile> all() {
    return GokartLogFiles.all(ARCHIVE);
  }
}
