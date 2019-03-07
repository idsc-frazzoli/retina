// code by jph
package ch.ethz.idsc.demo.jph.lidar.hist;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Increment;

/** builds histogram of azimuth angles */
class RotationalHistogram implements LidarRayDataListener {
  final Tensor histogram = Array.zeros(VelodyneStatics.AZIMUTH_RESOLUTION);
  private Integer rotational_last = null;

  @Override // from LidarRayDataListener
  public void timestamp(int usec, int type) {
    // ---
  }

  @Override // from LidarRayDataListener
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (Objects.nonNull(rotational_last))
      histogram.set(Increment.ONE, VelodyneStatics.lookupAzimuth(rotational - rotational_last));
    rotational_last = rotational;
  }
}