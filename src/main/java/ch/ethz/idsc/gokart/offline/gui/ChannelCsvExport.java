// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import ch.ethz.idsc.gokart.offline.channel.SingleChannelInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Put;

public enum ChannelCsvExport {
  ;
  /** @param file
   * @param target_folder to dump csv.gz files
   * @throws IOException */
  public static void of(GokartLcmMap gokartLcmMap, File target_folder) throws IOException {
    target_folder.mkdir();
    // ---
    Put.of(new File(target_folder, StaticHelper.LOG_START_TIME), RealScalar.of(gokartLcmMap.utime));
    // ---
    for (Entry<SingleChannelInterface, Tensor> entry : gokartLcmMap.map.entrySet())
      Export.of( //
          new File(target_folder, entry.getKey().exportName() + StaticHelper.EXTENSION), //
          entry.getValue().map(CsvFormat.strict()));
  }
}
