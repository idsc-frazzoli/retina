// code by jph
package ch.ethz.idsc.demo.jph.lidar.hist;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class PlanarHistogram implements LidarRayDataListener {
  public static final int MID = 2000;
  public static final int WID = 4001;
  // ---
  private final Map<Tensor, Integer> hash = new HashMap<>();
  private Integer rotational_last = null;
  private Integer usec_last = null;
  private Tensor tuple = Tensors.empty();

  @Override // from LidarRayDataListener
  public void timestamp(int usec, int type) {
    if (Objects.nonNull(usec_last)) {
      int delta = usec - usec_last;
      delta /= 10;
      int index = delta + MID;
      index = Math.min(Math.max(0, index), WID - 1);
      tuple.append(RealScalar.of(index));
    }
    usec_last = usec;
  }

  @Override // from LidarRayDataListener
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (Objects.nonNull(rotational_last)) {
      int index = VelodyneStatics.lookupAzimuth(rotational - rotational_last);
      if (tuple.length() == 1) {
        tuple.append(RealScalar.of(index));
        if (!hash.containsKey(tuple))
          hash.put(tuple, 0);
        hash.put(tuple, hash.get(tuple) + 1);
        tuple = Tensors.empty();
      }
    }
    rotational_last = rotational;
  }

  public Tensor compile() {
    return Tensor.of(hash.entrySet().stream() //
        .map(entry -> entry.getKey().copy().append(RealScalar.of(entry.getValue()))));
  }
}