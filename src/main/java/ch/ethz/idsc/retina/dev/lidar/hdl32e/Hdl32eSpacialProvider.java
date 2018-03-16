// code by jph
package ch.ethz.idsc.retina.dev.lidar.hdl32e;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;

/** converts firing data to spacial events with time, 3d-coordinates and
 * intensity
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class Hdl32eSpacialProvider implements LidarSpacialProvider {
  public static final AngleVectorLookupFloat TRIGONOMETRY = new AngleVectorLookupFloat(36000, true, 0);
  // ---
  private final List<LidarSpacialListener> listeners = new LinkedList<>();
  /* package for testing */ int limit_lo = 10; // TODO choose reasonable value
  private int usec;

  @Override
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
    float dx = TRIGONOMETRY.dx(rotational);
    float dy = TRIGONOMETRY.dy(rotational);
    float[] coords = new float[3];
    for (int laser = 0; laser < Hdl32eDevice.INSTANCE.LASERS; ++laser) {
      int distance = byteBuffer.getShort() & 0xffff;
      int intensity = byteBuffer.get() & 0xff;
      if (limit_lo <= distance) {
        // "report distance to the nearest 0.2 cm" => 2 mm
        float range = distance * VelodyneStatics.TO_METER_FLOAT; // convert to [m]
        coords[0] = Hdl32eDevice.INSTANCE.IR[laser] * range * dx;
        coords[1] = Hdl32eDevice.INSTANCE.IR[laser] * range * dy;
        coords[2] = Hdl32eDevice.INSTANCE.IZ[laser] * range;
        LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, intensity);
        listeners.forEach(listener -> listener.lidarSpacial(lidarSpacialEvent));
      }
    }
  }
}
