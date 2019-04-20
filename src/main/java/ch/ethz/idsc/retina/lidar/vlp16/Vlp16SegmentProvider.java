// code by jph, ynager
package ch.ethz.idsc.retina.lidar.vlp16;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.retina.lidar.LidarXYZEvent;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;

/** converts firing data to spatial events with time, 3d-coordinates and
 * intensity. Only rays with an altitude angle of <= max_degree [deg] are processed. */
public class Vlp16SegmentProvider extends VelodyneSpacialProvider {
  private final List<Integer> laserList = new ArrayList<>();
  //  ---
  private final AngleVectorLookupFloat lookup;
  private final float[] IR;
  private final float[] IZ;

  /** @param angle_offset of azimuth so that x,y-system is aligned with robot coordinate system
   * @param max_degree until which rays are considered. For instance if max_degree == 0,
   * only rays with negative/downwards inclination are considered */
  public Vlp16SegmentProvider(double angle_offset, int max_degree) {
    for (int laserId = 0; laserId < 16; ++laserId)
      if (Vlp16Helper.degree(laserId) <= max_degree)
        laserList.add(laserId * 3);
    int size = laserList.size();
    IR = new float[size];
    IZ = new float[size];
    lookup = new AngleVectorLookupFloat(VelodyneStatics.AZIMUTH_RESOLUTION, true, angle_offset);
    for (int index = 0; index < size; ++index) {
      int laserId = laserList.get(index) / 3;
      double theta = Math.toRadians(Vlp16Helper.degree(laserId));
      IR[index] = (float) (Math.cos(theta) * VelodyneStatics.TO_METER);
      IZ[index] = (float) (Math.sin(theta) * VelodyneStatics.TO_METER);
    }
  }

  @Override // from LidarRayDataListener
  public void scan(int azimuth, ByteBuffer byteBuffer) {
    int bufferPos = byteBuffer.position();
    float dx = lookup.dx(azimuth);
    float dy = lookup.dy(azimuth);
    float[] coords = new float[3];
    for (int laser = 0; laser < laserList.size(); ++laser) {
      byteBuffer.position(bufferPos + laserList.get(laser));
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

  List<Integer> laserList() {
    return Collections.unmodifiableList(laserList);
  }
}
