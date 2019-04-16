// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/* package */ enum GokartLogCutter {
  ;
  public static void main(String[] args) throws IOException {
    GokartLogFile gokartLogFile = GokartLogFile._20190328T164433_ad28d651;
    File file = DatahakiLogFileLocator.file(gokartLogFile);
    // file = new File("/media/datahaki/data/gokart/cuts/20190401", "20190401T115537_411917b6.lcm.00");
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter( //
        gokartLogFileIndexer, //
        new File("/media/datahaki/data/gokart/cuts"), //
        gokartLogFile.getTitle());
  }
}
