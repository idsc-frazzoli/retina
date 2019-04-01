// code by jph
package ch.ethz.idsc.demo.am;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/* package */ enum GokartLogCutter {
  ;
  public static void main(String[] args) throws IOException {
    File file = new File("/media/datahaki/backup/gokartlogs/20190325/20190325T112618_cfdd7814.lcm.00");
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter( //
        gokartLogFileIndexer, //
        new File("/media/datahaki/data/gokart/cuts"), //
        "20190325T112618");
  }
}
