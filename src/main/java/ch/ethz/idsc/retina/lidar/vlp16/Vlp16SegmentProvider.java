// code by jph, ynager
package ch.ethz.idsc.retina.lidar.vlp16;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.retina.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;

/** converts firing data to spatial events with time, 3d-coordinates and
 * intensity. Only rays with an altitude angle of <= max_alt [deg] are processed. */
public class Vlp16SegmentProvider extends VelodyneSpacialProvider {
  private final List<Integer> laserList = new ArrayList<>();
  private final int NUM_LASERS;
  //  ---
  private final AngleVectorLookupFloat lookup;
  private final float[] IR;
  private final float[] IZ;
  // ---

  public Vlp16SegmentProvider(double angle_offset, int max_alt) {
    for (int i = 0; i < 16; i++)
      if (StaticHelper.degree(i) <= max_alt)
        laserList.add(i);
    NUM_LASERS = laserList.size();
    IR = new float[NUM_LASERS];
    IZ = new float[NUM_LASERS];
    lookup = new AngleVectorLookupFloat(VelodyneStatics.AZIMUTH_RESOLUTION, true, angle_offset);
    System.out.println("Rays processed at theta = ");
    for (int i = 0; i < NUM_LASERS; i++) {
      int laser = laserList.get(i);
      double theta = StaticHelper.degree(laser) * Math.PI / 180;
      System.out.print(StaticHelper.degree(laser) + "°,");
      IR[i] = (float) (Math.cos(theta) * VelodyneStatics.TO_METER);
      IZ[i] = (float) (Math.sin(theta) * VelodyneStatics.TO_METER);
    }
    System.out.println();
  }

  @Override // from LidarRayDataListener
  public void scan(int azimuth, ByteBuffer byteBuffer) {
    int bufferPos = byteBuffer.position();
    float dx = lookup.dx(azimuth);
    float dy = lookup.dy(azimuth);
    float[] coords = new float[3];
    for (int laser = 0; laser < NUM_LASERS; ++laser) {
      byteBuffer.position(bufferPos + laserList.get(laser) * 3); // TODO LHF pre-multiply
      int distance = byteBuffer.getShort() & 0xffff;
      byte intensity = byteBuffer.get();
      if (limit_lo <= distance) {
        float radius = IR[laser] * distance;
        coords[0] = radius * dx;
        coords[1] = radius * dy;
        coords[2] = IZ[laser] * distance;
        LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, intensity);
        listeners.forEach(listener -> listener.lidarSpacial(lidarSpacialEvent));
      }
    }
  }
}
