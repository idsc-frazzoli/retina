// code by gjoel
package ch.ethz.idsc.retina.lidar.vlp16;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.lidar.LidarPolarEvent;
import ch.ethz.idsc.retina.lidar.VelodynePolarProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;

public class Vlp16PolarProvider extends VelodynePolarProvider {
  private static final int LASERS = 16;
  private static final float FACTOR = (float) (2 * Math.PI / VelodyneStatics.AZIMUTH_RESOLUTION);

  @Override // from LidarRayDataListener
  public void scan(int azimuth, ByteBuffer byteBuffer) {
    float[] coords = new float[3];
    for (int laser = 0; laser < LASERS; ++laser) {
      int distance = byteBuffer.getShort() & 0xffff;
      byte intensity = byteBuffer.get();
      if (limit_lo <= distance) {
        // TODO JPH only convert values where needed
        coords[0] = azimuth * FACTOR; // azimuth in [rad]
        coords[1] = (float) Math.toRadians(StaticHelper.degree(laser)); // elevation in [rad]
        coords[2] = distance * VelodyneStatics.TO_METER_FLOAT; // distance in [m]
        LidarPolarEvent lidarPolarEvent = new LidarPolarEvent(usec, coords, intensity);
        listeners.forEach(listener -> listener.lidarPolar(lidarPolarEvent));
      }
    }
  }
}
