// code by vc
package ch.ethz.idsc.retina.dev.lidar.app;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;

/** extracts points at horizontal level, or arbitrary inclination for Velodyne VLP-16 */
public class TiltedVelodynePlanarEmulator implements LidarSpacialProvider {
  private final List<LidarSpacialListener> listeners = new LinkedList<>();
  /* package for testing */ int limit_lo = VelodyneStatics.DEFAULT_LIMIT_LO;
  private int usec;
  private final VelodyneRayLookup velodyneRayLookup;

  /** @param angle_offset
   * @param tiltY
   * @param emulation_deg */
  public TiltedVelodynePlanarEmulator(double angle_offset, double tiltY, double emulation_deg) {
    velodyneRayLookup = new VelodyneRayLookup(1, true, angle_offset, tiltY, emulation_deg);
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
    VelodyneRay velodyneRay = velodyneRayLookup.velodyneRay(rotational);
    byteBuffer.position(byteBuffer.position() + velodyneRay.index);
    // "report distance to the nearest 0.2 cm" => 2 mm
    int distance = byteBuffer.getShort() & 0xffff;
    if (limit_lo <= distance) {
      int intensity = byteBuffer.get() & 0xff;
      LidarSpacialEvent lidarSpacialEvent = //
          new LidarSpacialEvent(usec, velodyneRay.getCoord(distance), intensity);
      listeners.forEach(listener -> listener.lidarSpacial(lidarSpacialEvent));
    }
  }
}
