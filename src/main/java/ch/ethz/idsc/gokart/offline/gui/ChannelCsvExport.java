// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import ch.ethz.idsc.gokart.offline.channel.SingleChannelInterface;
import ch.ethz.idsc.gokart.offline.pose.GokartPosePostChannel;
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
  public static void of(GokartLcmMap gokartLcmMap, File target_folder) throws IOException {
    target_folder.mkdir();
    // ---
    Put.of(new File(target_folder, StaticHelper.LOG_START_TIME), RealScalar.of(gokartLcmMap.utime));
    // ---
    for (Entry<SingleChannelInterface, Tensor> entry : gokartLcmMap.map.entrySet())
      Export.of( //
          new File(target_folder, entry.getKey().exportName() + StaticHelper.EXTENSION), //
          entry.getValue().map(CsvFormat.strict()));
    // ---
    {
      Tensor pose = gokartLcmMap.map.get(GokartPosePostChannel.INSTANCE).copy();
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
