// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import ch.ethz.idsc.retina.lidar.app.LidarPanorama;
import ch.ethz.idsc.retina.lidar.app.LidarPanoramaProvider;

public class Vlp16PanoramaProvider extends LidarPanoramaProvider {
  /** constructor multiplies index values with image width */
  private static final int[] INDEX = new int[] { //
      15, 7, //
      14, 6, //
      13, 5, //
      12, 4, //
      11, 3, //
      10, 2, //
      9, 1, //
      8, 0 };

  public Vlp16PanoramaProvider(Supplier<LidarPanorama> supplier) {
    super(supplier);
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
