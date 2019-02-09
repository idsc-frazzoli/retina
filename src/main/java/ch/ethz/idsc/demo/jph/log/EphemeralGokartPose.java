// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.pose.GokartPosePostChannel;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum EphemeralGokartPose {
  ;
  public static void main(String[] args) throws IOException {
    File root = new File("/media/datahaki/data/gokart/cuts/20190204");
    for (File folder : root.listFiles())
      if (folder.isDirectory()) {
        File file = new File(folder, "post.lcm");
        if (file.isFile()) {
          OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(GokartPosePostChannel.INSTANCE);
          OfflineLogPlayer.process(file, offlineTableSupplier);
          Tensor tensor = offlineTableSupplier.getTable().map(CsvFormat.strict());
          File dest = HomeDirectory.file("Projects/ephemeral/src/main/resources/dubilab/app/pose/5m");
          Export.of(new File(dest, folder.getName() + ".csv"), tensor.map(Round._6));
        } else
          System.err.println("skip " + folder);
      }
  }
}
