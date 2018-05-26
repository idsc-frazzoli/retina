// code by jph
package ch.ethz.idsc.demo.jph;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.offline.api.LogFile;

enum GokartLogFileDemo {
  ;
  public static void main(String[] args) {
    LogFile logFile = GokartLogFile._20180522T144106_2da7e1f5;
    DatahakiLogFileLocator.file(logFile);
  }
}
