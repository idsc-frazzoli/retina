// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.lidar.LidarXYZEvent;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;

/** converts firing data to spacial events with time, 3d-coordinates and intensity
 * 
 * assumes rigid and approximate horizontal mounting of sensor,
 * i.e. any tilt is not compensated for */
public class Vlp16SpacialProvider extends VelodyneSpacialProvider {
  private static final int LASERS = 16;
  // ---
  /** converts azimuth to x, y-system that is aligned with robot coordinate system */
  private final AngleVectorLookupFloat lookup;
  /** factors based on inclination of ray/index of ray that also account for conversion to meter */
  private final float[] IR = new float[LASERS];
  private final float[] IZ = new float[LASERS];

  /** @param angle_offset of azimuth so that x, y-system is aligned with robot coordinate system */
  public Vlp16SpacialProvider(double angle_offset) {
    lookup = new AngleVectorLookupFloat(VelodyneStatics.AZIMUTH_RESOLUTION, true, angle_offset);
    for (int laser = 0; laser < LASERS; ++laser) {
      double theta = Math.toRadians(Vlp16Helper.degree(laser));
      IR[laser] = (float) (Math.cos(theta) * VelodyneStatics.TO_METER);
      IZ[laser] = (float) (Math.sin(theta) * VelodyneStatics.TO_METER);
    }
  }

  @Override // from LidarRayDataListener
  public void scan(int azimuth, ByteBuffer byteBuffer) {
    float dx = lookup.dx(azimuth);
    float dy = lookup.dy(azimuth);
    float[] coords = new float[3];
    for (int laser = 0; laser < LASERS; ++laser) {
      int distance = byteBuffer.getShort() & 0xffff;
      byte intensity = byteBuffer.get();
      if (limit_lo <= distance) {
        float radius = IR[laser] * distance;
        coords[0] = radius * dx;
        coords[1] = radius * dy;
        coords[2] = IZ[laser] * distance;
        LidarXYZEvent lidarXYZEvent = new LidarXYZEvent(usec, coords, intensity);
        listeners.forEach(listener -> listener.lidarSpacial(lidarXYZEvent));
      }
    }
  }
}
