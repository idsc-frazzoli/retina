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
import ch.ethz.idsc.gokart.offline.pose.LapLogSplitPredicate;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum LapLogSplit {
  ;
  public static void main(String[] args) throws IOException {
    File folder = new File("/media/datahaki/data/gokart/cuts/20190318/20190318T142605_08");
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    LogSplitPredicate logSplitPredicate = new LapLogSplitPredicate( //
        Tensors.vector(41.6, 34.2).multiply(Quantity.of(1, SI.METER)), //
        AngleVector.of(RealScalar.of(-2.25)));
    LogSplit lapSegmenter = new LogSplit(logSplitPredicate);
    OfflineLogPlayer.process(gokartLogInterface.file(), lapSegmenter);
    new LcmLogFileCutter(gokartLogInterface.file(), lapSegmenter.navigableMap()) {
      @Override
      public File filename(int index) {
        return new File("/home/datahaki/laps", String.format("r%02d.lcm", index));
      }
    };
  }
}
