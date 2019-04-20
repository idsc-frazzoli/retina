// code by jph, ynager
package ch.ethz.idsc.gokart.core.perc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Helper;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;

/** converts firing data to spatial events with time, 3d-coordinates and
 * intensity. Only rays with an altitude angle of <= max_degree [deg] are processed. */
// TODO JPH super class is not 100% suitable
public abstract class GokartSegmentProjection extends VelodyneSpacialProvider {
  private final List<Integer> laserList = new ArrayList<>();
  //  ---
  private final SpacialXZObstaclePredicate spacialXZObstaclePredicate = //
      SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final AngleVectorLookupFloat lookup;
  private final float[] IR;
  private final float[] IZ;
  private final float max_width;

  /** @param angle_offset of azimuth so that x,y-system is aligned with robot coordinate system
   * @param max_degree until which rays are considered. For instance if max_degree == 0,
   * only rays with negative/downwards inclination are considered */
  public GokartSegmentProjection(double angle_offset, double height, int max_degree) {
    for (int laserId = 0; laserId < 16; ++laserId)
      if (Vlp16Helper.degree(laserId) <= max_degree)
        laserList.add(laserId * 3);
    max_width = max_degree < 0 //
        ? (float) (height / Math.tan(Math.toRadians(-max_degree)))
        : 100f;
    System.out.println("max_width=" + max_width);
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

  private boolean truncate = true;

  @Override // from LidarRayDataListener
  public void scan(int azimuth, ByteBuffer byteBuffer) {
    if ((1000 < azimuth && azimuth < 17000) || (19000 < azimuth && azimuth < 35000)) {
      truncate = true;
      int position = byteBuffer.position();
      float dx = lookup.dx(azimuth);
      float dy = lookup.dy(azimuth);
      float min_radius = max_width;
      for (int laser = 0; laser < laserList.size(); ++laser) {
        byteBuffer.position(position + laserList.get(laser));
        int distance = byteBuffer.getShort() & 0xffff;
        if (limit_lo <= distance) {
          float radius = IR[laser] * distance;
          float x = radius * dx; // x
          float z = IZ[laser] * distance; // z
          if (spacialXZObstaclePredicate.isObstacle(x, z))
            min_radius = Math.min(min_radius, radius);
        }
      }
      freeSpaceUntil(azimuth, min_radius * dx, min_radius * dy);
    } else {
      if (truncate) {
        freeSpaceUntil(azimuth, 0, 0);
        truncate = false;
      }
    }
  }

  public abstract void freeSpaceUntil(int azimuth, float x, float y);
}
