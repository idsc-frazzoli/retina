// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.LcmLogFileCutter;
import ch.ethz.idsc.gokart.lcm.LogSplit;
import ch.ethz.idsc.gokart.lcm.LogSplitPredicate;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.pose.LineLogSplitPredicate;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum LineLogSplit {
  ;
  public static void main(String[] args) throws IOException {
    File folder = new File("/media/datahaki/data/gokart/ensemble/centerline");
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    LogSplitPredicate logSplitPredicate = new LineLogSplitPredicate( //
        Tensors.fromString("{37.37[m], 42.41[m], -2.11}"), Quantity.of(1, "m"));
    LogSplit logSplit = new LogSplit(logSplitPredicate);
    OfflineLogPlayer.process(gokartLogInterface.file(), logSplit);
    new LcmLogFileCutter(gokartLogInterface.file(), logSplit.navigableMap()) {
      @Override
      public File filename(int index) {
        return new File("/home/datahaki/laps", String.format("m%02d.lcm", index));
      }
    };
  }
}
