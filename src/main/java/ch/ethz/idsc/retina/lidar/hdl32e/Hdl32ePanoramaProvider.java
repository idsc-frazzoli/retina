// code by jph
package ch.ethz.idsc.retina.lidar.hdl32e;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.lidar.app.GrayscaleLidarPanorama;
import ch.ethz.idsc.retina.lidar.app.LidarPanoramaProvider;

public class Hdl32ePanoramaProvider extends LidarPanoramaProvider {
  /** at motor RPM == 600 the max width ~2170 at motor RPM == 1200 the max width
   * ~1083 */
  private static final int MAX_WIDTH = 2304;
  /** constructor multiplies index values with image width */
  private final int[] INDEX = new int[] { //
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
    super(() -> new GrayscaleLidarPanorama(MAX_WIDTH, Hdl32eDevice.INSTANCE.LASERS));
    IntStream.range(0, INDEX.length).forEach(i -> INDEX[i] *= MAX_WIDTH);
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    lidarPanorama.setRotational(rotational);
    for (int index : INDEX)
      lidarPanorama.setReading( //
          index, //
          byteBuffer.getShort() & 0xffff, //
          byteBuffer.get()); // 255 == most intensive return
  }
}
