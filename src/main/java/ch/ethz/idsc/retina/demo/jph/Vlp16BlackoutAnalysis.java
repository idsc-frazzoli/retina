// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class Vlp16BlackoutAnalysis implements OfflineTableSupplier, LidarRayDataListener {
  private static final int GAPSIZE = 2000;
  // ---
  private final Vlp16Decoder vlp16Decoder = new Vlp16Decoder();
  private Scalar time;
  private Integer usec_last;
  private Integer rota_last;
  private int delta_time;
  boolean flag = false;
  final TensorBuilder tensorBuilder = new TensorBuilder();

  public Vlp16BlackoutAnalysis() {
    vlp16Decoder.addRayListener(this);
  }

  @Override
  public void timestamp(int usec, int type) {
    if (Objects.nonNull(usec_last)) {
      delta_time = usec - usec_last;
      flag = delta_time > GAPSIZE;
    }
    usec_last = usec;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (flag) {
      int delta_angle = (rotational - rota_last + 36000) % 36000;
      tensorBuilder.flatten( //
          time.map(Magnitude.SECOND), //
          Tensors.vector(delta_time * 1e-3, delta_angle / 100.));
      flag = false;
    }
    rota_last = rotational;
  }

  @Override //
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("vlp16.center.ray")) {
      this.time = time;
      vlp16Decoder.lasers(byteBuffer);
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tensorBuilder.getTensor();
  }

  public static void main(String[] args) throws IOException {
    OfflineProcessing.INSTANCE.handle(() -> new Vlp16BlackoutAnalysis());
  }
}
