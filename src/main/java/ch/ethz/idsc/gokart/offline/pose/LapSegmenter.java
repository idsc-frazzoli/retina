// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.gokart.core.map.StartingCriteria;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.lcm.LcmLogFileCutter;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class LapSegmenter implements OfflineLogListener {
  private final NavigableMap<Integer, Integer> navigableMap = new TreeMap<>();
  private Tensor pose_prev;
  private int index = 0;
  private Integer lo = null;

  public LapSegmenter(Tensor pose) {
    pose_prev = pose;
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartPoseChannel.INSTANCE.channel())) {
      Tensor pose_next = new GokartPoseEvent(byteBuffer).getPose();
      boolean crossing = StartingCriteria.getLineTrigger(pose_prev.extract(0, 2), pose_next.extract(0, 2));
      // RandomVariate.of(BernoulliDistribution.of(RealScalar.of(0.01))).equals(RealScalar.ONE);
      if (crossing) {
        if (Objects.nonNull(lo))
          navigableMap.put(lo, index - 1);
        lo = index;
      }
      pose_prev = pose_next;
    }
    ++index;
  }

  public static void main(String[] args) throws IOException {
    File folder = new File("/media/datahaki/data/gokart/cuts/20190318/20190318T142605_08");
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    LapSegmenter lapSegmenter = new LapSegmenter(gokartLogInterface.pose());
    OfflineLogPlayer.process(gokartLogInterface.file(), lapSegmenter);
    new LcmLogFileCutter(gokartLogInterface.file(), lapSegmenter.navigableMap) {
      @Override
      public File filename(int index) {
        return new File("/home/datahaki/laps", String.format("r%02d.lcm", index));
      }
    };
  }
}
