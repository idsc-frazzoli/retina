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
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.TableBuilder;

public class Vlp16BlackoutTable implements OfflineTableSupplier, LidarRayDataListener {
  private static final int GAPSIZE = 2000;
  private static final String LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Vlp16Decoder vlp16Decoder = new Vlp16Decoder();
  // ---
  private Scalar time;
  private Integer usec_last;
  private Integer rota_last;
  private int delta_time;
  private boolean flag = false;

  public Vlp16BlackoutTable() {
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
      int delta_angle = VelodyneStatics.lookupAzimuth(rotational - rota_last);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND), //
          Tensors.vector(delta_time * 1e-3, delta_angle / 100.));
      flag = false;
    }
    rota_last = rotational;
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
