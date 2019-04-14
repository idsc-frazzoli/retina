// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.offline.video.BasicTrackTable;
import ch.ethz.idsc.tensor.io.HomeDirectory;

enum RunBasicTrackTable {
  ;
  public static void main(String[] args) {
    File folder = HomeDirectory.file("ensemblelaps/pursuit");
    List<BasicTrackTable> list = Stream.of(folder.listFiles()) //
        .filter(File::isFile) //
        .map(BasicTrackTable::from) //
        .sorted() //
        .collect(Collectors.toList());
    for (BasicTrackTable gokartRaceFile : list)
      System.out.println(gokartRaceFile.file.getName() + " " + gokartRaceFile.duration());
  }
}
