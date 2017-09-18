// code by jph
package ch.ethz.idsc.retina.dev.lidar.vlp16;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.dev.lidar.app.LidarGrayscalePanorama;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanorama;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.tensor.RealScalar;

public class Vlp16PanoramaProvider extends LidarPanoramaProvider {
  /** at motor RPM == 600 the max width ~2170 at motor RPM == 1200 the max width
   * ~1083 */
  private static final int MAX_WIDTH = 2304;
  /** constructor multiplies index values with image width */
  private final int[] index = new int[] { //
      15, 7, //
      14, 6, //
      13, 5, //
      12, 4, //
      11, 3, //
      10, 2, //
      9, 1, //
      8, 0 };

  // ---
  public Vlp16PanoramaProvider() {
    IntStream.range(0, index.length).forEach(i -> index[i] *= MAX_WIDTH);
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    final int x = lidarPanorama.getWidth();
    lidarPanorama.setAngle(RealScalar.of(rotational));
    if (x < MAX_WIDTH) {
      for (int laser = 0; laser < 16; ++laser) {
        int distance = byteBuffer.getShort() & 0xffff;
        byte intensity = byteBuffer.get(); // 255 == most intensive return
        lidarPanorama.setReading(x + index[laser], distance * VelodyneStatics.TO_METER_FLOAT, intensity);
      }
    } else
      System.err.println("width <= " + x);
  }

  @Override
  protected LidarPanorama supply() {
    return new LidarGrayscalePanorama(MAX_WIDTH, 16);
  }
}
