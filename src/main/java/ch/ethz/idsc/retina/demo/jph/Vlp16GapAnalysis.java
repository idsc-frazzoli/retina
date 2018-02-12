// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.Round;

class Vlp16GapAnalysis implements OfflineTableSupplier, LidarRayDataListener {
  private static final Scalar GAPSIZES = RealScalar.of(200);
  private static final Mod MOD = Mod.function(36000);
  private static final String LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final Vlp16Decoder vlp16Decoder = new Vlp16Decoder();
  private final TensorBuilder tensorBuilder = new TensorBuilder();
  private Tensor row = null;
  private Scalar time;

  public Vlp16GapAnalysis() {
    vlp16Decoder.addRayListener(this);
  }

  @Override
  public void timestamp(int usec, int type) {
    if (Objects.nonNull(row)) {
      Tensor diff = MOD.of(Differences.of(row));
      if (diff.stream().map(Scalar.class::cast) //
          .anyMatch(da -> Scalars.lessThan(GAPSIZES, da))) {
        System.out.println(time);
        tensorBuilder.flatten(time.map(Magnitude.SECOND).map(Round._6), row);
      }
    }
    row = Tensors.empty();
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    row.append(RealScalar.of(rotational));
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(LIDAR)) {
      this.time = time;
      vlp16Decoder.lasers(byteBuffer);
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tensorBuilder.getTensor();
  }

  public static void main(String[] args) throws IOException {
    OfflineProcessing.handle(() -> new Vlp16GapAnalysis());
  }
}
