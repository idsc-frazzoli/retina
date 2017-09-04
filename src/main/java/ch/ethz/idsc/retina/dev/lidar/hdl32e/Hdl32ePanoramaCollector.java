// code by jph
package ch.ethz.idsc.retina.dev.lidar.hdl32e;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.dev.lidar.app.LidarGrayscalePanorama;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanorama;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaListener;
import ch.ethz.idsc.tensor.RealScalar;

public class Hdl32ePanoramaCollector implements LidarRayDataListener {
  /** at motor RPM == 600 the max width ~2170
   * at motor RPM == 1200 the max width ~1083 */
  private static final int MAX_WIDTH = 2304;
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
  private final List<LidarPanoramaListener> lidarPanoramaListeners = new LinkedList<>();
  private final Supplier<LidarPanorama> supplier = () -> new LidarGrayscalePanorama(MAX_WIDTH, Hdl32eDevice.LASERS);
  private LidarPanorama lidarPanorama = supplier.get();

  public Hdl32ePanoramaCollector() {
    IntStream.range(0, index.length).forEach(i -> index[i] *= MAX_WIDTH);
  }

  public void addListener(LidarPanoramaListener lidarPanoramaListener) {
    lidarPanoramaListeners.add(lidarPanoramaListener);
  }

  @Override
  public void timestamp(int usec, int type) {
    // ---
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (rotational < rotational_last) {
      lidarPanoramaListeners.forEach(listener -> listener.panorama(lidarPanorama));
      lidarPanorama = supplier.get();
    }
    rotational_last = rotational;
    final int x = lidarPanorama.getWidth();
    lidarPanorama.setAngle(RealScalar.of(rotational));
    if (x < MAX_WIDTH) {
      for (int laser = 0; laser < Hdl32eDevice.LASERS; ++laser) {
        int distance = byteBuffer.getShort() & 0xffff;
        byte intensity = byteBuffer.get(); // 255 == most intensive return
        lidarPanorama.setReading(x + index[laser], distance * VelodyneStatics.TO_METER_FLOAT, intensity);
      }
    } else
      System.err.println("width <= " + x);
  }
}
