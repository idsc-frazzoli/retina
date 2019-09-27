// code by jph
package ch.ethz.idsc.demo.jph.race;

import java.io.File;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.track.ManualTrackLayoutDemo;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;

/** used in analysis of race on 20190701 between human driver and dynamic mpc
 * 
 * https://github.com/idsc-frazzoli/retina/files/3492127/20190812_autonomous_human_racing.pdf */
/* package */ enum RunManualTrackLayoutDemo {
  ;
  public static void main(String[] args) throws Exception {
    // OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(GokartPoseChannel.INSTANCE);
    // OfflineLogPlayer.process(new File("/media/datahaki/data/gokart/mpccmp/20190909/20190909T174744_00/log.lcm"), offlineTableSupplier);
    // Tensor tensor = offlineTableSupplier.getTable();
    // System.out.println(Dimensions.of(tensor));
    ManualTrackLayoutDemo manualTrackLayoutDemo = new ManualTrackLayoutDemo();
    File file = HomeDirectory.file("20190921T124329_track/controlpoints.csv");
    if (file.isFile()) {
      Tensor points = Import.of(file);
      System.out.println(Dimensions.of(points));
      if (Objects.nonNull(points))
        manualTrackLayoutDemo.setControlPointsSe2(points);
    }
    // manualTrackLayoutDemo.setCurveR2(Tensor.of(tensor.stream().map(row -> row.extract(1, 3))));
    manualTrackLayoutDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    manualTrackLayoutDemo.timerFrame.jFrame.setVisible(true);
  }
}
