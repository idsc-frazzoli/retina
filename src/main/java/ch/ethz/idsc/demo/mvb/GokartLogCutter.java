// code by jph
package ch.ethz.idsc.demo.mvb;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum GokartLogCutter {
  ;
  public static void main(String[] args) throws IOException {
    GokartLogFile gokartLogFile = GokartLogFile._20190404T143912_39258d17;
    File file = HomeDirectory.file( //
        "0_ETH/01_MasterThesis/Logs_GoKart/LogData/dynamics", //
        gokartLogFile.getFilename());
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter( //
        gokartLogFileIndexer, //
        HomeDirectory.file("0_ETH/01_MasterThesis/Logs_GoKart/LogData/dynamics/cuts"), //
        gokartLogFile.getTitle());
  }
}
