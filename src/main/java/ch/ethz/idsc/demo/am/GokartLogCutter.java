//code by jph
package ch.ethz.idsc.demo.am;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/* package */ enum GokartLogCutter {
  ;
  public static void main(String[] args) throws IOException {
    /** original log file */
    File file = new File("/Users/antoniamosberger/Documents/01_6_Semester/Bachelorarbeit/20190418(1)/20190418T145229p2/log.lcm");
    /** destination folder */
    File dest = new File("/Users/antoniamosberger/Documents/01_6_Semester/Bachelorarbeit/cuts1");
    /** title of subdirectory, usually identical to log file name above */
    String name = "20190418_2";
    dest.mkdir();
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter(gokartLogFileIndexer, dest, name);
  }
}