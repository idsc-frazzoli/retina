// code by jph
package ch.ethz.idsc.demo.jph.race;

import java.io.File;

import ch.ethz.idsc.gokart.core.track.ManualTrackLayoutDemo;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;

/** used in analysis of race on 20190701 between human driver and dynamic mpc
 * 
 * https://github.com/idsc-frazzoli/retina/files/3492127/20190812_autonomous_human_racing.pdf */
/* package */ enum RunManualTrackLayoutDemo {
  ;
  public static void main(String[] args) throws Exception {
    OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(GokartPoseChannel.INSTANCE);
    OfflineLogPlayer.process(new File("/media/datahaki/data/gokart/mpccmp/20190909/20190909T174744_00/log.lcm"), offlineTableSupplier);
    Tensor tensor = offlineTableSupplier.getTable();
    System.out.println(Dimensions.of(tensor));
    ManualTrackLayoutDemo manualTrackLayoutDemo = new ManualTrackLayoutDemo();
    // Tensor points = ResourceData.of("/dubilab/analysis/track/20190701.csv");
    // if (Objects.nonNull(points))
    // manualTrackLayoutDemo.setControlPointsSe2(points);
    manualTrackLayoutDemo.setCurveR2(Tensor.of(tensor.stream().map(row -> row.extract(1, 3))));
    manualTrackLayoutDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    manualTrackLayoutDemo.timerFrame.jFrame.setVisible(true);
  }
}
