// code by jph
package ch.ethz.idsc.retina.dev.lidar.vlp16;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanorama;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaProvider;

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
    for (int laser = 0; laser < 16; ++laser) {
      int distance = byteBuffer.getShort() & 0xffff;
      byte intensity = byteBuffer.get(); // 255 == most intensive return
      lidarPanorama.setReading(INDEX[laser], distance * VelodyneStatics.TO_METER_FLOAT, intensity);
    }
  }
}
