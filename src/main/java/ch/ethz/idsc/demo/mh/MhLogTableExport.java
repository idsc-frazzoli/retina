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
    // files.add(UserHome.file("20180430T104113_a5291af9.lcm.00"));
    files.add(UserHome.file("Racing/20180820T1438521.lcm"));
    files.add(UserHome.file("Racing/20180820T1438522.lcm"));
    files.add(UserHome.file("Racing/20180820T1438523.lcm"));
    files.add(UserHome.file("Racing/20180820T1438524.lcm"));
    files.add(UserHome.file("Racing/20180820T1438525.lcm"));
    files.add(UserHome.file("Racing/20180820T1438526.lcm"));
    files.add(UserHome.file("Racing/20180820T1656371.lcm"));
    files.add(UserHome.file("Racing/20180820T1656372.lcm"));
    files.add(UserHome.file("Racing/20180820T1656373.lcm"));
    files.add(UserHome.file("Racing/20180820T1656374.lcm"));
    files.add(UserHome.file("Racing/20180820T1656375.lcm"));
    //
    for (File inputFile : files)
      try {
        systemAnalysis.process(inputFile);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}
