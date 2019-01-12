// code by jph
package ch.ethz.idsc.retina.lidar.mark8;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.lidar.app.GrayscaleLidarPanorama;
import ch.ethz.idsc.retina.lidar.app.LidarPanoramaProvider;

public class Mark8PanoramaProvider extends LidarPanoramaProvider {
  private static final int MAX_WIDTH = 5360;
  /** constructor multiplies index values with image width */
  private final int[] index = new int[8];

  public Mark8PanoramaProvider() {
    super(() -> new GrayscaleLidarPanorama(MAX_WIDTH, 8));
    IntStream.range(0, index.length).forEach(i -> index[i] = (7 - i) * MAX_WIDTH);
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // final int x = lidarPanorama.getWidth();
    // lidarPanorama.setAngle(RealScalar.of(rotational));
    lidarPanorama.setRotational(rotational);
    for (int laser = 0; laser < 8; ++laser) {
      int distance = byteBuffer.getShort() & 0xffff;
      byte intensity = byteBuffer.get(); // 255 == most intensive return
      lidarPanorama.setReading(index[laser], distance, intensity);
    }
  }
}
