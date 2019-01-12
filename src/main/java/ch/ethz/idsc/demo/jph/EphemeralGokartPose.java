// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.GokartPoseTable;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum EphemeralGokartPose {
  ;
  public static void main(String[] args) throws IOException {
    File root = new File("/media/datahaki/media/ethz/gokart/topic/localization");
    File dest = HomeDirectory.file("Projects/ephemeral/src/main/resources/dubilab/app/pose/gyro");
    for (File folder : root.listFiles())
      if (folder.isDirectory()) {
        System.out.println(folder);
        GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
        // ---
        OfflineTableSupplier offlineTableSupplier = GokartPoseTable.all();
        OfflineLogPlayer.process(gokartLogInterface.file(), offlineTableSupplier);
        Tensor tensor = offlineTableSupplier.getTable().map(CsvFormat.strict());
        Export.of(new File(dest, folder.getName() + ".csv"), tensor);
      }
  }
}
