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

/* package */ enum EphemeralGokartPose {
  ;
  public static void main(String[] args) throws IOException {
    File root = new File(StaticHelper.CUTS, "20190321");
    for (File folder : root.listFiles())
      if (folder.isDirectory()) {
        File file = new File(folder, StaticHelper.POST_LCM);
        if (file.isFile()) {
          OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(GokartPosePostChannel.INSTANCE);
          OfflineLogPlayer.process(file, offlineTableSupplier);
          Tensor tensor = offlineTableSupplier.getTable().map(CsvFormat.strict());
          File dest = HomeDirectory.file("Projects/ephemeral/src/main/resources/dubilab/app/pose", root.getName());
          dest.mkdir();
          Export.of(new File(dest, folder.getName() + ".csv"), tensor);
        } else
          System.err.println("skip " + folder);
      }
  }
}
