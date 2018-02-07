// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.demo.DubendorfHangarLog;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.NSingle;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;

enum OfflineProcessing {
  INSTANCE;
  // ---
  private final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public void handle(Supplier<OfflineTableSupplier> supplier) throws IOException {
    for (DubendorfHangarLog dubendorfHangarLog : DubendorfHangarLog.values()) {
      File file = dubendorfHangarLog.file(LOG_ROOT);
      if (file.isFile()) {
        System.out.println(dubendorfHangarLog.title());
        single(file, supplier.get(), dubendorfHangarLog.title());
      } else
        System.err.println(dubendorfHangarLog);
      // break;
    }
  }

  public static void single(File file, OfflineTableSupplier offlineTableSupplier, String title) throws IOException {
    OfflineLogPlayer.process(file, offlineTableSupplier);
    Tensor table = offlineTableSupplier.getTable();
    Export.of(UserHome.file(title + ".csv"), table.map(NSingle.FUNCTION));
  }
}
