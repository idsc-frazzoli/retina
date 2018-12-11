// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16SpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class ProximityAnalysis implements LidarSpacialListener, OfflineTableSupplier {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private static final Scalar PARTITION = RealScalar.of(20);
  private static final Scalar THRESHOLD = RealScalar.of(1.0);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final Vlp16SpacialProvider lidarSpacialProvider = //
      new Vlp16SpacialProvider(SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue());
  private final Map<Tensor, Integer> map = new HashMap<>();

  public ProximityAnalysis() {
    // TODO setLimitLo
    // lidarSpacialProvider.setLimitLo(0.0);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    lidarSpacialProvider.addListener(this);
  }

  @Override // from LidarSpacialListener
  public void lidarSpacial(LidarSpacialEvent lidarSpacialEvent) {
    Tensor coords = Tensors.vectorFloat(lidarSpacialEvent.coords);
    if (Scalars.lessThan(Norm.INFINITY.ofVector(coords), THRESHOLD)) {
      Tensor key = Round.of(coords.multiply(PARTITION));
      if (map.containsKey(key))
        map.put(key, map.get(key) + 1);
      else
        map.put(key, 1);
    }
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (CHANNEL_LIDAR.equals(channel))
      velodyneDecoder.lasers(byteBuffer);
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return Tensor.of(map.entrySet().stream() //
        .map(entry -> entry.getKey().divide(PARTITION).map(Round._2).append(RealScalar.of(entry.getValue()))));
  }
}
