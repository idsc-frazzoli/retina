// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.ethz.idsc.gokart.lcm.LogStartTime;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.SingleChannelInterface;
import ch.ethz.idsc.gokart.offline.pose.GokartPosePostChannel;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.sca.Round;

public enum ChannelCsvExport {
  ;
  /** @param file
   * @param target_folder to dump csv.gz files
   * @throws IOException */
  public static void of(File file, File target_folder) throws IOException {
    if (!file.isFile())
      throw new RuntimeException("" + file);
    target_folder.mkdir();
    Map<SingleChannelInterface, OfflineTableSupplier> map = StaticHelper.SINGLE_CHANNEL_INTERFACES.stream() //
        .collect(Collectors.toMap(Function.identity(), SingleChannelTable::of));
    long utime = LogStartTime.utime(file);
    Put.of(new File(target_folder, StaticHelper.LOG_START_TIME), RealScalar.of(utime));
    // ---
    OfflineLogPlayer.process(file, map.values());
    for (Entry<SingleChannelInterface, OfflineTableSupplier> entry : map.entrySet())
      Export.of( //
          new File(target_folder, entry.getKey().exportName() + StaticHelper.EXTENSION), //
          entry.getValue().getTable().map(CsvFormat.strict()));
    // ---
    {
      Tensor pose = map.get(GokartPosePostChannel.INSTANCE).getTable().copy();
      Tensor tensor = Tensor.of(pose.stream().map(row -> row.extract(1, 4)));
      Tensor smooth = GokartPoseSmoothing.INSTANCE.apply(tensor).map(Round._6);
      for (int index = 0; index < 3; ++index)
        pose.set(smooth.get(Tensor.ALL, index), Tensor.ALL, 1 + index);
      if (0 < pose.length())
        Export.of( //
            new File(target_folder, StaticHelper.GOKART_POSE_SMOOTH + StaticHelper.EXTENSION), //
            pose);
    }
  }
}
