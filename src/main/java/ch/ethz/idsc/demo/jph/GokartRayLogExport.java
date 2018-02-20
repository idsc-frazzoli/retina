// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.sca.Increment;

class RotationalHistogram implements LidarRayDataListener {
  public static final int MOD = 36000;
  // ---
  Tensor histogram = Array.zeros(MOD);
  private Integer rotational_last = null;

  @Override
  public void timestamp(int usec, int type) {
    // ---
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (Objects.nonNull(rotational_last)) {
      int index = rotational - rotational_last;
      index += MOD;
      index %= MOD;
      histogram.set(Increment.ONE, index);
    }
    rotational_last = rotational;
  }
}

class TemporalHistogram implements LidarRayDataListener {
  public static final int MID = 2000;
  public static final int WID = 4001;
  // ---
  Tensor histogram = Array.zeros(WID);
  private Integer usec_last = null;

  @Override
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

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // ---
  }
}

class PlanarHistogram implements LidarRayDataListener {
  public static final int MOD = 36000;
  public static final int MID = 2000;
  public static final int WID = 4001;
  // ---
  private Integer rotational_last = null;
  private Integer usec_last = null;
  Tensor tuple = Tensors.empty();
  Map<Tensor, Integer> hash = new HashMap<>();

  @Override
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

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (Objects.nonNull(rotational_last)) {
      int index = rotational - rotational_last;
      index += MOD;
      index %= MOD;
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

enum GokartRayLogExport {
  ;
  public static void main(String[] args) throws IOException {
    final String channel = VelodyneLcmChannels.ray(VelodyneModel.VLP16, "center");
    Vlp16Decoder vlp16Decoder = new Vlp16Decoder();
    RotationalHistogram listener = new RotationalHistogram();
    vlp16Decoder.addRayListener(listener);
    TemporalHistogram temporalHistogram = new TemporalHistogram();
    vlp16Decoder.addRayListener(temporalHistogram);
    PlanarHistogram planarHistogram = new PlanarHistogram();
    vlp16Decoder.addRayListener(planarHistogram);
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, String _channel, ByteBuffer byteBuffer) {
        if (_channel.equals(channel))
          vlp16Decoder.lasers(byteBuffer);
      }
    };
    File file = new File("/media/datahaki/media/ethz/gokartlogs", "20180112T105400_9e1d3699.lcm.00");
    OfflineLogPlayer.process(file, offlineLogListener);
    Put.of(UserHome.file("ray_angles.wmt"), listener.histogram);
    Put.of(UserHome.file("ray_times.wmt"), temporalHistogram.histogram);
    Put.of(UserHome.file("ray_planar.wmt"), planarHistogram.compile());
  }
}
