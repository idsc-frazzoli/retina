// code by jph
package ch.ethz.idsc.demo.jph.lidar.hist;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Increment;

class TemporalHistogram implements LidarRayDataListener {
  public static final int MID = 2000;
  public static final int WID = 4001;
  // ---
  final Tensor histogram = Array.zeros(WID);
  private Integer usec_last = null;

  @Override // from LidarRayDataListener
  public void timestamp(int usec, int type) {
    if (Objects.nonNull(usec_last)) {
      int delta = usec - usec_last;
      delta /= 10;
      // System.out.println(usec - usec_last);
      int index = delta + MID;
      index = Math.min(Math.max(0, index), WID - 1);
      histogram.set(Increment.ONE, index);
    }
    usec_last = usec;
  }

  @Override // from LidarRayDataListener
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // ---
  }
}