// code by jph
package ch.ethz.idsc.retina.dev.lidar.hdl32e;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.dev.lidar.app.LidarGrayscalePanorama;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanorama;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.tensor.RealScalar;

public class Hdl32ePanoramaProvider extends LidarPanoramaProvider {
  /** at motor RPM == 600 the max width ~2170 at motor RPM == 1200 the max width
   * ~1083 */
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
  public Hdl32ePanoramaProvider() {
    IntStream.range(0, index.length).forEach(i -> index[i] *= MAX_WIDTH);
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    final int x = lidarPanorama.getWidth();
    lidarPanorama.setAngle(RealScalar.of(rotational));
    if (x < MAX_WIDTH) {
      for (int laser = 0; laser < Hdl32eDevice.INSTANCE.LASERS; ++laser) {
        int distance = byteBuffer.getShort() & 0xffff;
        byte intensity = byteBuffer.get(); // 255 == most intensive return
        lidarPanorama.setReading(x + index[laser], distance * VelodyneStatics.TO_METER_FLOAT, intensity);
      }
    } else
      System.err.println("width <= " + x);
  }

  @Override
  protected LidarPanorama supply() {
    return new LidarGrayscalePanorama(MAX_WIDTH, Hdl32eDevice.INSTANCE.LASERS);
  }
}
