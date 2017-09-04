// code by jph
package ch.ethz.idsc.retina.dev.lidar.mark8;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.dev.lidar.app.LidarGrayscalePanorama;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanorama;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.tensor.RealScalar;

public class Mark8PanoramaProvider extends LidarPanoramaProvider {
  private static final int MAX_WIDTH = 5360;
  /** constructor multiplies index values with image width */
  private final int[] index = new int[8];

  // ---
  public Mark8PanoramaProvider() {
    IntStream.range(0, index.length).forEach(i -> index[i] = (7 - i) * MAX_WIDTH);
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    final int x = lidarPanorama.getWidth();
    lidarPanorama.setAngle(RealScalar.of(rotational));
    if (x < MAX_WIDTH) {
      for (int laser = 0; laser < 8; ++laser) {
        int distance = byteBuffer.getShort() & 0xffff;
        byte intensity = byteBuffer.get(); // 255 == most intensive return
        lidarPanorama.setReading(x + index[laser], distance * VelodyneStatics.TO_METER_FLOAT, intensity);
      }
    } else
      System.err.println("width <= " + x);
  }

  @Override
  protected LidarPanorama supply() {
    return new LidarGrayscalePanorama(MAX_WIDTH, 8);
  }
}
