// code by jph
package ch.ethz.idsc.demo.mg;

import java.io.IOException;

import ch.ethz.idsc.gokart.offline.tab.PoseFilteringTable;
import ch.ethz.idsc.tensor.io.HomeDirectory;

enum PoseFilterDemo {
  ;
  public static void main(String[] args) throws IOException {
    PoseFilteringTable.process(LogFileLocations.DUBISiliconEyeH.getFile(), HomeDirectory.file("poseFiltering"));
  }
}
