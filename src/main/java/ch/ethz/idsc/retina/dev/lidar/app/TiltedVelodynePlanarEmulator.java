// code by vc
package ch.ethz.idsc.retina.dev.lidar.app;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;
import ch.ethz.idsc.tensor.sca.ArcTan;

/** extracts points at horizontal level, or arbitrary inclination for Velodyne VLP-16 */
// TODO make more general: enable emulation of any angle (not just +1[deg])
public class TiltedVelodynePlanarEmulator implements LidarSpacialProvider {
  private final List<LidarSpacialListener> listeners = new LinkedList<>();
  /* package for testing */ int limit_lo = 10; // TODO choose reasonable value
  private final double emulation_deg;
  private final double tiltY_deg;
  private int usec;
  private final AngleVectorLookupFloat lookup;

  /** @param angle_offset
   * @param tiltY
   * @param emulation_deg */
  public TiltedVelodynePlanarEmulator(double angle_offset, double tiltY, double emulation_deg) {
    lookup = new AngleVectorLookupFloat(36000, true, angle_offset);
    tiltY_deg = Math.toDegrees(tiltY);
    this.emulation_deg = emulation_deg;
  }

  @Override // from LidarSpacialProvider
  public void addListener(LidarSpacialListener lidarSpacialEventListener) {
    listeners.add(lidarSpacialEventListener);
  }

  /** quote from the user's manual, p.8: "the minimum return distance for the
   * HDL-32E is approximately 1 meter. ignore returns closer than this"
   * 
   * however, we find that in office conditions correct ranges below 1 meter are
   * provided
   * 
   * @param closest in [m] */
  public void setLimitLo(double closest) {
    limit_lo = (int) (closest / VelodyneStatics.TO_METER);
  }

  @Override // from LidarRayDataListener
  public void timestamp(int usec, int type) {
    this.usec = usec;
  }

  /** @param tilt_deg
   * @return */
  int closestRay(double tilt_deg) {
    return (int) (2 * Math.round((tilt_deg + emulation_deg - 1) / 2) + 1);
  }

  @Override // from LidarRayDataListener
  public void scan(int rotational, ByteBuffer byteBuffer) {
    float dx = lookup.dx(rotational);
    float dy = lookup.dy(rotational);
    double angle = ArcTan.of(dx, dy).number().doubleValue();
    double tilt = tiltY_deg * Math.cos(angle); // Math.toDegrees(0.04) * cos(angle)
    final float[] coords = new float[2];
    int index = degreeToLidarID(closestRay(tilt));
    byteBuffer.position(byteBuffer.position() + index * 3);
    int distance = byteBuffer.getShort() & 0xffff;
    int intensity = byteBuffer.get() & 0xff;
    if (limit_lo <= distance) {
      // "report distance to the nearest 0.2 cm" => 2 mm
      float range = distance * VelodyneStatics.TO_METER_FLOAT; // convert to [m]
      coords[0] = range * dx;
      coords[1] = range * dy;
      LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, intensity);
      listeners.forEach(listener -> listener.lidarSpacial(lidarSpacialEvent));
    }
  }

  /** @param degree in {-15, -13, -11, -1, +1, +3, ..., +15}
   * @return lidar ID */
  static int degreeToLidarID(int degree) {
    return (degree + 15) % 15;
  }
}
