// code by jph
package ch.ethz.idsc.demo.az;

import java.io.File;

import ch.ethz.idsc.gokart.offline.api.LogFile;
import ch.ethz.idsc.gokart.offline.api.LogFileLocator;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum AleLogFileLocator implements LogFileLocator {
  INSTANCE;
  // ---
  private static final File LOG_ROOT = HomeDirectory.file("datasets", "gokartlogs");

  @Override
  public File getAbsoluteFile(LogFile logFile) {
    String title = logFile.getFilename();
    String date = title.substring(0, 8);
    {
      File file = new File(new File(LOG_ROOT, date), title);
      if (file.isFile())
        return file;
    }
    throw new RuntimeException("not found: " + title);
  }

  public static File file(LogFile logFile) {
    return INSTANCE.getAbsoluteFile(logFile);
  }
}
