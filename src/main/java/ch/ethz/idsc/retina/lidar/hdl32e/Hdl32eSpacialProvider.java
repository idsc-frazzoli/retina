// code by jph
package ch.ethz.idsc.retina.lidar.hdl32e;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;

/** converts firing data to spacial events with time, 3d-coordinates and
 * intensity
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class Hdl32eSpacialProvider extends VelodyneSpacialProvider {
  public static final AngleVectorLookupFloat TRIGONOMETRY = //
      new AngleVectorLookupFloat(VelodyneStatics.AZIMUTH_RESOLUTION, true, 0);
  // ---

  @Override // from LidarRayDataListener
  public void scan(int rotational, ByteBuffer byteBuffer) {
    float dx = TRIGONOMETRY.dx(rotational);
    float dy = TRIGONOMETRY.dy(rotational);
    float[] coords = new float[3];
    for (int laser = 0; laser < Hdl32eDevice.INSTANCE.LASERS; ++laser) {
      int distance = byteBuffer.getShort() & 0xffff;
      byte intensity = byteBuffer.get();
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
