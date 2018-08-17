// code by mh
package ch.ethz.idsc.demo.mh;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.subare.util.UserHome;

/* package */ enum MhLogTableExport {
  ;
  public static void main(String[] args) {
    //
    File outputFolder = UserHome.file("retina_out"); // MH modify if necessary
    ComprehensiveLogTableExport systemAnalysis = new ComprehensiveLogTableExport(outputFolder);
    //
    List<File> files = new LinkedList<>();
    files.add(UserHome.file("20180611T095800_851c404d.lcm.00"));
    //
    for (File inputFile : files)
      try {
        systemAnalysis.process(inputFile);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}
