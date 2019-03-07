// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.Round;

public class Vlp16GapTable implements OfflineTableSupplier, LidarRayDataListener {
  private static final Scalar GAPSIZES = RealScalar.of(200);
  private static final Mod MOD = Mod.function(VelodyneStatics.AZIMUTH_RESOLUTION);
  private static final String LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final Vlp16Decoder vlp16Decoder = new Vlp16Decoder();
  private final TableBuilder tableBuilder = new TableBuilder();
  // ---
  private Tensor row = null;
  private Scalar time;

  public Vlp16GapTable() {
    vlp16Decoder.addRayListener(this);
  }

  @Override
  public void timestamp(int usec, int type) {
    if (Objects.nonNull(row)) {
      Tensor diff = MOD.of(Differences.of(row));
      if (diff.stream().map(Scalar.class::cast) //
          .anyMatch(da -> Scalars.lessThan(GAPSIZES, da))) {
        System.out.println(time);
        tableBuilder.appendRow(time.map(Magnitude.SECOND).map(Round._6), row);
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
    return tableBuilder.toTable();
  }
}
