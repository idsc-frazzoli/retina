// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e.data;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.dev.velodyne.LidarRayDataListener;
import ch.ethz.idsc.tensor.RealScalar;

public class Hdl32ePanoramaCollector implements LidarRayDataListener {
  private static final int LASERS = 32;
  /** constructor multiplies index values with image width */
  private final int[] index = new int[] { //
      31, 15, //
      30, 14, //
      29, 13, //
      28, 12, //
      27, 11, //
      26, 10, //
      25, 9, //
      24, 8, //
      23, 7, //
      22, 6, //
      21, 5, //
      20, 4, //
      19, 3, //
      18, 2, //
      17, 1, //
      16, 0 };
  // ---
  private int rotational_last = -1;
  private final List<Hdl32ePanoramaListener> hdl32ePanoramaListeners = new LinkedList<>();
  private final Supplier<Hdl32ePanorama> supplier = () -> new Hdl32eHuePanorama();
  private Hdl32ePanorama hdl32ePanorama = supplier.get();

  public Hdl32ePanoramaCollector() {
    IntStream.range(0, index.length).forEach(i -> index[i] *= Hdl32ePanorama.MAX_WIDTH);
  }

  public void addListener(Hdl32ePanoramaListener hdl32ePanoramaListener) {
    hdl32ePanoramaListeners.add(hdl32ePanoramaListener);
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (rotational < rotational_last) {
      hdl32ePanoramaListeners.forEach(listener -> listener.panorama(hdl32ePanorama));
      hdl32ePanorama = supplier.get();
    }
    rotational_last = rotational;
    final int x = hdl32ePanorama.getWidth();
    hdl32ePanorama.setAngle(RealScalar.of(rotational));
    if (x < Hdl32ePanorama.MAX_WIDTH) {
      for (int laser = 0; laser < LASERS; ++laser) {
        int distance = byteBuffer.getShort() & 0xffff;
        byte intensity = byteBuffer.get(); // 255 == most intensive return
        // ---
        hdl32ePanorama.setReading(x, index[laser], distance, intensity);
      }
    } else {
      System.err.println("width <= " + x);
    }
  }

  @Override
  public void timestamp(int usec, byte type) {
    // ---
  }
}
