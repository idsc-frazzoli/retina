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
    File file = new File("/Users/antoniamosberger/Documents/01_6_Semester/Bachelorarbeit/lcm_files/20190530/20190530T153744_1f4048bb.lcm.00");
    /** destination folder */
    File dest = new File("/Users/antoniamosberger/Documents/01_6_Semester/Bachelorarbeit/cuts1");
    /** title of subdirectory, usually identical to log file name above */
    String name = "20190530_2";
    dest.mkdir();
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter(gokartLogFileIndexer, dest, name);
  }
}