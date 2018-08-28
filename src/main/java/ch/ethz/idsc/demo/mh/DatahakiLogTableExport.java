// code by jph
package ch.ethz.idsc.demo.mh;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.subare.util.UserHome;

/* package */ enum DatahakiLogTableExport {
  ;
  public static void main(String[] args) throws IOException {
    ComprehensiveLogTableExport systemAnalysis = new ComprehensiveLogTableExport(UserHome.file("testout"));
    // File file = DatahakiLogFileLocator.file(GokartLogFile._20180814T175821_2c569ed8);
    File file = DatahakiLogFileLocator.file(GokartLogFile._20180430T104113_a5291af9);
    systemAnalysis.process(file);
  }
}
