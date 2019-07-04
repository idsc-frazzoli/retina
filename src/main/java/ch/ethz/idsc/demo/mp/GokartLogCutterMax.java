// code by jph
package ch.ethz.idsc.demo.mp;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/* package */ enum GokartLogCutterMax {
  ;
  public static void main(String[] args) throws IOException {
    /** original log file */
    File file = new File("/home/maximilien/Downloads/20190627T133639_12dcbfa8.lcm.00");
    // File file = new File("/home/maximilien/Downloads/20190401T101109_411917b6.lcm.00");
    /** destination folder */
    File dest = new File("/home/maximilien/Documents/sp/logs/");
    /** title of subdirectory, usually identical to log file name above */
    String name = "20190401T101109";
    dest.mkdir();
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter(gokartLogFileIndexer, dest, name);
  }
}
