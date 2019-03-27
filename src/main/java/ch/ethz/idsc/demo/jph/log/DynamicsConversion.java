// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
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

/* package */ enum DynamicsConversion {
  ;
  public static Optional<File> single(File cut) {
    File file = new File(cut, StaticHelper.POST_LCM);
    if (!file.isFile())
      throw new RuntimeException("" + file);
    // ---
    File folder = new File(StaticHelper.DEST, cut.getName().substring(0, 8)); // date e.g. 20190208
    folder.mkdir();
    File target = new File(folder, cut.getName());
    if (target.isDirectory())
      return Optional.empty();
    // ---
    target.mkdir();
    Map<SingleChannelInterface, OfflineTableSupplier> map = StaticHelper.SINGLE_CHANNEL_INTERFACES.stream() //
        .collect(Collectors.toMap(Function.identity(), SingleChannelTable::of));
    try {
      long utime = LogStartTime.utime(file);
      Put.of(new File(target, StaticHelper.LOG_START_TIME), RealScalar.of(utime));
      // ---
      OfflineLogPlayer.process(file, map.values());
      for (Entry<SingleChannelInterface, OfflineTableSupplier> entry : map.entrySet())
        Export.of( //
            new File(target, entry.getKey().exportName() + StaticHelper.EXTENSION), //
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
              new File(target, StaticHelper.GOKART_POSE_SMOOTH + StaticHelper.EXTENSION), //
              pose);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return Optional.of(target);
  }
}
