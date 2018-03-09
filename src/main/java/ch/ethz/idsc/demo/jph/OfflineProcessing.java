// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import ch.ethz.idsc.demo.DubendorfHangarLog;
import ch.ethz.idsc.demo.LogFileInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

// TODO not public
enum OfflineProcessing {
  ;
  private static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void handle(Supplier<OfflineTableSupplier> supplier) throws IOException {
    handle(Arrays.asList(DubendorfHangarLog.values()), supplier);
  }

  public static void handle(Collection<? extends LogFileInterface> collection, Supplier<OfflineTableSupplier> supplier) //
      throws IOException {
    for (LogFileInterface logFileInterface : collection) {
      File file = logFileInterface.file(LOG_ROOT);
      if (file.isFile()) {
        System.out.println(logFileInterface.title());
        single(file, supplier.get(), logFileInterface.title());
      } else
        System.err.println(logFileInterface);
      // break;
    }
  }

  public static void single(File file, OfflineTableSupplier offlineTableSupplier, String title) throws IOException {
    OfflineLogPlayer.process(file, offlineTableSupplier);
    Tensor table = offlineTableSupplier.getTable();
    if (Tensors.isEmpty(table)) {
      System.err.println("skip export: table is empty");
      return;
    }
    if (!MatrixQ.of(table))
      System.err.println("export does not have matrix structure");
    Export.of(UserHome.file(title + ".csv"), table.map(CsvFormat.strict()));
  }
}
