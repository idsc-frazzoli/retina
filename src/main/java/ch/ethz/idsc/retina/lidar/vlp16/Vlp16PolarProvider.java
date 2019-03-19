// code by gjoel
package ch.ethz.idsc.retina.lidar.vlp16;

import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.lidar.LidarPolarEvent;
import ch.ethz.idsc.retina.lidar.VelodynePolarProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;

import java.nio.ByteBuffer;

public class Vlp16PolarProvider extends VelodynePolarProvider {
  private static final int LASERS = 16;
  // ---
  private final double offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();

  @Override // from LidarRayDataListener
  public void scan(int azimuth, ByteBuffer byteBuffer) {
    float[] coords = new float[3];
    for (int laser = 0; laser < LASERS; ++laser) {
      int distance = byteBuffer.getShort() & 0xffff;
      byte intensity = byteBuffer.get();
      if (limit_lo <= distance) {
        coords[0] = (float) (2 * Math.PI * azimuth / VelodyneStatics.AZIMUTH_RESOLUTION + offset); // azimuth in [rad]
        coords[1] = (float) Math.toRadians(StaticHelper.degree(laser)); // elevation in [rad]
        coords[2] = distance * VelodyneStatics.TO_METER_FLOAT; // distance in [m]
        LidarPolarEvent lidarPolarEvent = new LidarPolarEvent(usec, coords, intensity);
        listeners.forEach(listener -> listener.lidarSpacial(lidarPolarEvent));
      }
    }
  }
}
