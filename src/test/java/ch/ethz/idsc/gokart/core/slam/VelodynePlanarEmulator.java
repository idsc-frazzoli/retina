// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;

/** CLASS IS NOT IN USE ANYMORE, INSTEAD USE TiltedVelodynePlanarEmulator
 * 
 * extracts points at horizontal level for velodyne */
/* package */ class VelodynePlanarEmulator implements LidarSpacialProvider {
  public static VelodynePlanarEmulator hdl32e(double angle_offset) {
    return new VelodynePlanarEmulator(angle_offset, 15); // index of horizontal beam == 15
  }

  public static VelodynePlanarEmulator vlp16_p01deg(double angle_offset) {
    return new VelodynePlanarEmulator(angle_offset, 1); // index of beam with 1 degree inclination == +1
  }

  /** observation in Dubendorf: 1[deg] down typically hits the floor in 40[m]
   * 
   * @return */
  /* package */ static VelodynePlanarEmulator vlp16_n01deg(double angle_offset) {
    return new VelodynePlanarEmulator(angle_offset, 14); // index of beam with 1 degree inclination == -1
  }
  // ---

  private final List<LidarSpacialListener> listeners = new LinkedList<>();
  /* package for testing */ int limit_lo = VelodyneSpacialProvider.INITIAL_LIMIT_LO;
  private int usec;
  private final AngleVectorLookupFloat lookup;
  private final int index;

  /** @param index of horizontal laser */
  public VelodynePlanarEmulator(double angle_offset, int index) {
    lookup = new AngleVectorLookupFloat(36000, true, angle_offset);
    this.index = index;
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

  @Override // from LidarRayDataListener
  public void scan(int rotational, ByteBuffer byteBuffer) {
    float dx = lookup.dx(rotational);
    float dy = lookup.dy(rotational);
    final float[] coords = new float[2];
    byteBuffer.position(byteBuffer.position() + index * 3);
    int distance = byteBuffer.getShort() & 0xffff;
    byte intensity = byteBuffer.get();
    if (limit_lo <= distance) {
      // "report distance to the nearest 0.2 cm" => 2 mm
      float range = distance * VelodyneStatics.TO_METER_FLOAT; // convert to [m]
      coords[0] = range * dx;
      coords[1] = range * dy;
      LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, intensity);
      listeners.forEach(listener -> listener.lidarSpacial(lidarSpacialEvent));
    }
  }
}
