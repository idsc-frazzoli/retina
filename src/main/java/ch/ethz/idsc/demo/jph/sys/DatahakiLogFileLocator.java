// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.demo.GokartLogFiles;
import ch.ethz.idsc.gokart.offline.api.LogFile;
import ch.ethz.idsc.gokart.offline.api.LogFileLocator;
import ch.ethz.idsc.owl.bot.util.UserHome;

public enum DatahakiLogFileLocator implements LogFileLocator {
  INSTANCE;
  // ---
  /** the archive of all log files is kept on an external hard-drive */
  private static final File ARCHIVE = new File("/media/datahaki/backup/gokartlogs");
  private static final List<File> LOG_ROOT = Arrays.asList( //
      ARCHIVE, //
      UserHome.file("gokartlogs"));

  @Override // from LogFileLocator
  public File getAbsoluteFile(LogFile logFile) {
    String title = logFile.getFilename();
    String date = title.substring(0, 8); // e.g. "20180924"
    for (File dir : LOG_ROOT) {
      File file = new File(new File(dir, date), title);
      if (file.isFile())
        return file;
    }
    return null;
  }

  /** @param logFile
   * @return
   * @throws Exception if file cannot be located */
  // TODO comment not in sync with implementation
  public static File file(LogFile logFile) {
    return INSTANCE.getAbsoluteFile(logFile);
  }

  /** @return */
  public static Collection<LogFile> all() {
    return GokartLogFiles.all(ARCHIVE);
  }
}
