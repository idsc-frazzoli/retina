// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.Round;

class Vlp16RateAnalysis implements OfflineTableSupplier, LidarRayDataListener {
  private static final String DAVIS = DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  private static final String LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private static final Mod MOD = Mod.function(36000);
  // ---
  private final Vlp16Decoder vlp16Decoder = new Vlp16Decoder();
  private final TensorBuilder tensorBuilder = new TensorBuilder();
  private Integer usec_last = null;
  private Tensor row = null;
  private Scalar time;
  private DavisImuFrame dif;

  public Vlp16RateAnalysis() {
    vlp16Decoder.addRayListener(this);
  }

  @Override
  public void timestamp(int usec, int type) {
    if (Objects.nonNull(row) && Objects.nonNull(dif)) {
      Scalar gap = MOD.apply(row.Get(23).subtract(row.Get(0)));
      tensorBuilder.flatten( //
          time.map(Magnitude.SECOND).map(Round._6), //
          RealScalar.of(usec_last), //
          dif.gyroImageFrame().map(Magnitude.ANGULAR_RATE), //
          gap);
    }
    row = Tensors.empty();
    usec_last = usec;
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
    } else //
    if (channel.equals(DAVIS)) {
      dif = new DavisImuFrame(byteBuffer);
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tensorBuilder.getTensor();
  }

  public static void main(String[] args) throws IOException {
    OfflineProcessing.single( //
        UserHome.file("temp/20180108T165210_manual.lcm"), //
        new Vlp16RateAnalysis(), //
        "maxtorque");
  }
}
