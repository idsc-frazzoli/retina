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
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;

public class GokartLcmMap {
  public final File file;
  public final Map<SingleChannelInterface, Tensor> map;
  public final long utime;

  public GokartLcmMap(File file) throws IOException {
    this.file = file;
    Map<SingleChannelInterface, OfflineTableSupplier> map = StaticHelper.SINGLE_CHANNEL_INTERFACES.stream() //
        .collect(Collectors.toMap(Function.identity(), SingleChannelTable::of));
    OfflineLogPlayer.process(file, map.values());
    this.map = map.entrySet().stream() //
        .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getTable()));
    utime = LogStartTime.utime(file);
  }

  /** Hint: constructor exists for testing only */
  /* package */ GokartLcmMap() {
    file = HomeDirectory.file("empty_log.lcm");
    Map<SingleChannelInterface, OfflineTableSupplier> map = StaticHelper.SINGLE_CHANNEL_INTERFACES.stream() //
        .collect(Collectors.toMap(Function.identity(), SingleChannelTable::of));
    this.map = map.entrySet().stream() //
        .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getTable()));
    utime = 0;
  }
}
