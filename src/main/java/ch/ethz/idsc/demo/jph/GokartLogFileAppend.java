// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.offline.api.LogFile;

/** print out is template code to append in
 * {@link GokartLogFile} */
/* package */ enum GokartLogFileAppend {
  ;
  public static void main(String[] args) {
    GokartLogFile[] values = GokartLogFile.values();
    GokartLogFile last = values[values.length - 1];
    System.out.println(last.getFilename() + " <--- last ");
    for (LogFile logFile : DatahakiLogFileLocator.all()) {
      File file = DatahakiLogFileLocator.file(logFile);
      if (50_000_000 < file.length())
        if (last.getFilename().compareTo(logFile.getFilename()) < 0) {
          System.out.println("/** */");
          System.out.println("_" + logFile.getFilename().substring(0, 24) + ",");
        }
    }
  }
}
