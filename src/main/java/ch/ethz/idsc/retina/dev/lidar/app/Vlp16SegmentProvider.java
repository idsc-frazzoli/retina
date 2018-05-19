// code by jph, ynager
package ch.ethz.idsc.retina.dev.lidar.app;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16SpacialProvider;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;

/** converts firing data to spacial events with time, 3d-coordinates and
 * intensity. Only rays with an altitude angle of <= max_alt [deg] are processed. */
public class Vlp16SegmentProvider implements LidarSpacialProvider {
  private final List<Integer> laserList = new ArrayList<>();
  private final int NUM_LASERS;
  //  ---
  private final AngleVectorLookupFloat lookup;
  private final float[] IR;
  private final float[] IZ;
  // ---
  private final List<LidarSpacialListener> listeners = new LinkedList<>();
  /* package for testing */ int limit_lo = VelodyneStatics.DEFAULT_LIMIT_LO;
  private int usec;

  public Vlp16SegmentProvider(double angle_offset, int max_alt) {
    for (int i = 0; i < 16; i++)
      if (Vlp16SpacialProvider.degree(i) <= max_alt)
        laserList.add(i);
    NUM_LASERS = laserList.size();
    IR = new float[NUM_LASERS];
    IZ = new float[NUM_LASERS];
    lookup = new AngleVectorLookupFloat(36000, true, angle_offset);
    System.out.println("Rays processed at theta = ");
    for (int i = 0; i < NUM_LASERS; i++) {
      int laser = laserList.get(i);
      double theta = Vlp16SpacialProvider.degree(laser) * Math.PI / 180;
      System.out.print(Vlp16SpacialProvider.degree(laser) + "°,");
      IR[i] = (float) Math.cos(theta);
      IZ[i] = (float) Math.sin(theta);
    }
    System.out.println();
  }

  /** quote from the user's manual, p.8: "the minimum return distance for the
   * HDL-32E is approximately 1 meter. ignore returns closer than this"
   * 
   * @param closest
   * in [m] */
  public void setLimitLo(double closest) {
    limit_lo = (int) (closest / VelodyneStatics.TO_METER);
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
  public void scan(int azimuth, ByteBuffer byteBuffer) {
    int bufferPos = byteBuffer.position();
    float dx = lookup.dx(azimuth);
    float dy = lookup.dy(azimuth);
    float[] coords = new float[3];
    for (int i = 0; i < NUM_LASERS; ++i) {
      byteBuffer.position(bufferPos + laserList.get(i) * 3);
      int distance = byteBuffer.getShort() & 0xffff;
      int intensity = byteBuffer.get() & 0xff;
      if (limit_lo <= distance) {
        // "report distance to the nearest 0.2 cm" => 2 mm
        float range = distance * VelodyneStatics.TO_METER_FLOAT; // convert to [m]
        coords[0] = IR[i] * range * dx;
        coords[1] = IR[i] * range * dy;
        coords[2] = IZ[i] * range;
        LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, intensity);
        listeners.forEach(listener -> listener.lidarSpacial(lidarSpacialEvent));
      }
    }
  }
}
