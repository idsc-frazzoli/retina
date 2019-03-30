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
import ch.ethz.idsc.gokart.offline.pose.ThrottleSplitPredicate;

/* package */ enum ThrottleLogSplit {
  ;
  public static void main(String[] args) throws IOException {
    File folder = new File("/media/datahaki/data/gokart/cuts/20190328/20190328T165416_06");
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    LogSplitPredicate logSplitPredicate = new ThrottleSplitPredicate();
    LogSplit lapSegmenter = new LogSplit(logSplitPredicate);
    OfflineLogPlayer.process(gokartLogInterface.file(), lapSegmenter);
    new LcmLogFileCutter(gokartLogInterface.file(), lapSegmenter.navigableMap()) {
      @Override
      public File filename(int index) {
        return new File("/home/datahaki/laps", String.format("jh06%02d.lcm", index));
      }
    };
  }
}
