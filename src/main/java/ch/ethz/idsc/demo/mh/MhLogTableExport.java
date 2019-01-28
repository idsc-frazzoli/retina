// code by mh
package ch.ethz.idsc.demo.mh;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum MhLogTableExport {
  ;
  public static void main(String[] args) {
    //
    File outputFolder = HomeDirectory.file("retina_out"); // MH modify if necessary
    ComprehensiveLogTableExport systemAnalysis = new ComprehensiveLogTableExport(outputFolder);
    //
    List<File> files = new LinkedList<>();
    // files.add(UserHome.file("20180430T104113_a5291af9.lcm.00"));
    // files.add(UserHome.file("Racing/20180820T1438521.lcm"));
    // files.add(UserHome.file("Racing/20180820T1438522.lcm"));
    // files.add(UserHome.file("Racing/20180820T1438523.lcm"));
    // files.add(UserHome.file("Racing/20180820T1438524.lcm"));
    // files.add(UserHome.file("Racing/20180820T1438525.lcm"));
    // files.add(UserHome.file("Racing/20180820T1438526.lcm"));
    // files.add(UserHome.file("Racing/20180820T1656371.lcm"));
    // files.add(UserHome.file("Racing/20180820T1656372.lcm"));
    // files.add(UserHome.file("Racing/20180820T1656373.lcm"));
    // files.add(UserHome.file("Racing/20180820T1656374.lcm"));
    // files.add(UserHome.file("Racing/20180820T1656375.lcm"));
    // files.add(UserHome.file("20180430T104113_a5291af9.lcm.00"));
    /* files.add(UserHome.file("Racing/20180820T1438521.lcm"));
     * files.add(UserHome.file("Racing/20180820T1438522.lcm"));
     * files.add(UserHome.file("Racing/20180820T1438523.lcm"));
     * files.add(UserHome.file("Racing/20180820T1438524.lcm"));
     * files.add(UserHome.file("Racing/20180820T1438525.lcm"));
     * files.add(UserHome.file("Racing/20180820T1438526.lcm"));
     * files.add(UserHome.file("Racing/20180820T1656371.lcm"));
     * files.add(UserHome.file("Racing/20180820T1656372.lcm"));
     * files.add(UserHome.file("Racing/20180820T1656373.lcm"));
     * files.add(UserHome.file("Racing/20180820T1656374.lcm"));
     * files.add(UserHome.file("Racing/20180820T1656375.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1336121.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1336122.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1544171.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1544172.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1544173.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1544174.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1800411.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1800412.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1800413.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1800414.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1800415.lcm"));
     * files.add(UserHome.file("Racing/0w/20180702T1800416.lcm"));
     * files.add(UserHome.file("Racing/0w/20180705T1019441.lcm"));
     * files.add(UserHome.file("Racing/0w/20180705T1019442.lcm"));
     * files.add(UserHome.file("Racing/0w/20180705T1019443.lcm"));
     * files.add(UserHome.file("forthandback/fab.lcm")); */
    // files.add(HomeDirectory.file("brakingtest.lcm"));
    // files.add(HomeDirectory.file("20190125T105720_ecbd24e3.lcm.00"));
    // files.add(HomeDirectory.file("20190125T134537_e5eb6f95.lcm.00"));
    files.add(HomeDirectory.file("20190128T141006_6f6e3dee.lcm.00"));
    for (File inputFile : files)
      try {
        systemAnalysis.process(inputFile);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}
