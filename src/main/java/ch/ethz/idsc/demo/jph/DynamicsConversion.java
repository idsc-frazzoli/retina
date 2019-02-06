// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.GokartPoseTable;
import ch.ethz.idsc.gokart.offline.tab.RimoGetChannel;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.io.Export;

enum DynamicsConversion {
  ;
  private static final File ROOT = new File("/media/datahaki/data/gokart/cuts");
  private static final File DEST = new File("/media/datahaki/data/gokart/dynamics");

  public static void main(String[] args) {
    for (File folder : ROOT.listFiles()) {
      for (File cut : folder.listFiles()) {
        System.out.println(cut);
        File dest = new File(DEST, cut.getName());
        dest.mkdir();
        File file = new File(cut, "log.lcm");
        OfflineTableSupplier pose = GokartPoseTable.all();
        OfflineTableSupplier rimoPut = new SingleChannelTable(new RimoGetChannel());
        try {
          OfflineLogPlayer.process(file, pose, rimoPut);
          Export.of(new File(dest, "pose.csv.gz"), pose.getTable());
          Export.of(new File(dest, "rimoPut.csv.gz"), rimoPut.getTable());
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }
  }
}
