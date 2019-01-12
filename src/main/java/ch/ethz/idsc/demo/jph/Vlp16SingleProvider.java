// code by jph
package ch.ethz.idsc.demo.jph;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;

/** used in {@link SensorRackVibration} */
/* package */ class Vlp16SingleProvider extends VelodyneSpacialProvider {
  private final int position_laser;

  /** @param angle_offset
   * @param laser 0 for -15[deg], 1 for 1[deg], 2 for -13[deg], ... */
  public Vlp16SingleProvider(double angle_offset, int laser) {
    position_laser = laser * 3;
  }

  @Override // from LidarRayDataListener
  public void scan(int azimuth, ByteBuffer byteBuffer) {
    float[] coords = new float[2];
    int position = byteBuffer.position();
    byteBuffer.position(position + position_laser);
    int distance = byteBuffer.getShort() & 0xffff;
    byte intensity = byteBuffer.get();
    coords[0] = azimuth;
    coords[1] = distance * VelodyneStatics.TO_METER_FLOAT;
    LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, intensity);
    listeners.forEach(listener -> listener.lidarSpacial(lidarSpacialEvent));
  }
}
