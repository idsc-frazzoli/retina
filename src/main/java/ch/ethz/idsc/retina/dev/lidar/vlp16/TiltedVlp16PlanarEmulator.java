// code by vc
package ch.ethz.idsc.retina.dev.lidar.vlp16;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.dev.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.app.VelodyneRay;

/** extracts points at horizontal level, for arbitrary inclination of Velodyne VLP-16 */
public class TiltedVlp16PlanarEmulator extends VelodyneSpacialProvider {
  private final List<LidarSpacialListener> listeners = new LinkedList<>();
  private final Vlp16RayLookup vlp16RayLookup;
  private int usec;

  /** @param bits: 0 no loss of precision, 1 half angular precision, ...
   * @param angle_offset
   * @param tiltY
   * @param emulation_deg */
  public TiltedVlp16PlanarEmulator(int bits, double angle_offset, double tiltY, double emulation_deg) {
    vlp16RayLookup = new Vlp16RayLookup(bits, true, angle_offset, tiltY, emulation_deg);
  }

  @Override // from LidarSpacialProvider
  public void addListener(LidarSpacialListener lidarSpacialEventListener) {
    listeners.add(lidarSpacialEventListener);
  }

  @Override // from LidarRayDataListener
  public void timestamp(int usec, int type) {
    this.usec = usec;
  }

  @Override // from LidarRayDataListener
  public void scan(int rotational, ByteBuffer byteBuffer) {
    VelodyneRay velodyneRay = vlp16RayLookup.velodyneRay(rotational);
    byteBuffer.position(byteBuffer.position() + velodyneRay.offset);
    // "report distance to the nearest 0.2 cm" => 2 mm
    int distance = byteBuffer.getShort() & 0xffff;
    if (limit_lo <= distance) {
      byte intensity = byteBuffer.get();
      LidarSpacialEvent lidarSpacialEvent = //
          new LidarSpacialEvent(usec, velodyneRay.getCoord(distance), intensity);
      listeners.forEach(listener -> listener.lidarSpacial(lidarSpacialEvent));
    }
  }
}
