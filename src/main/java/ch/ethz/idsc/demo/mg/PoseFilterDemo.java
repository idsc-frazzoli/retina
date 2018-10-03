package ch.ethz.idsc.demo.mg;

import java.io.IOException;

import ch.ethz.idsc.gokart.offline.tab.PoseFilteringTable;
import ch.ethz.idsc.owl.bot.util.UserHome;

enum PoseFilterDemo {
  ;
  public static void main(String[] args) throws IOException {
    PoseFilteringTable.process(LogFileLocations.DUBI19z.getFile(), UserHome.file("poseFiltering"));
  }
}
