// code by jph
package ch.ethz.idsc.retina.dev.quanergy.mark8;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.velodyne.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.velodyne.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.velodyne.LidarSpacialEventListener;

public class Mark8SpacialProvider implements LidarRayDataListener {
  private static final int LASERS = 8;
  private static final float[] IR = new float[8];
  private static final float[] IZ = new float[8];
  private static final double ANGLE_FACTOR = 2 * Math.PI / 10400.0;
  private static final double TO_METER = 0.00001;
  private static final float TO_METER_FLOAT = (float) TO_METER;
  /** ordering taken from p.33 */
  private static final int[] ORDERING = new int[] { 0, 4, 2, 6, 1, 5, 3, 7 };
  /** angles taken from p.33 */
  // TODO the values state in the data sheet are not evenly spaced... need confirmation through experiments
  private static final double[] M8_VERTICAL_ANGLES = { //
      -0.318505, -0.2692, -0.218009, -0.165195, -0.111003, -0.0557982, 0.0, 0.0557982 };
  // ---
  private final List<LidarSpacialEventListener> listeners = new LinkedList<>();

  public Mark8SpacialProvider() {
    for (int laser = 0; laser < LASERS; ++laser) {
      double theta = M8_VERTICAL_ANGLES[ORDERING[laser]];
      IR[laser] = (float) Math.cos(theta);
      IZ[laser] = (float) Math.sin(theta);
    }
  }

  public void addListener(LidarSpacialEventListener lidarSpacialEventListener) {
    listeners.add(lidarSpacialEventListener);
  }

  int usec;

  @Override
  public void timestamp(int usec, byte type) {
    this.usec = usec;
  }

  byte[] intensity = new byte[24];

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    final double angle = rotational * ANGLE_FACTOR;
    // new LidarSpacialEvent(usec, coords, intensity);
    float dx = (float) Math.cos(angle);
    float dy = (float) -Math.sin(angle);
    int position = byteBuffer.position();
    byteBuffer.position(position + 24 * 4);
    byteBuffer.get(intensity); // bulk read intensities
    byteBuffer.position(position);
    int index = 0;
    float[] coords = new float[3];
    for (int layer = 0; layer < 3; ++layer) { // TODO check if loops are correctly nested
      for (int laser = 0; laser < 8; ++laser) {
        // 0 indicates an invalid point
        int distance = byteBuffer.getInt(); // distances in 10 micrometers
        if (distance != 0) {
          float range = distance * TO_METER_FLOAT; // convert to [m]
          coords[0] = IR[laser] * range * dx;
          coords[1] = IR[laser] * range * dy;
          coords[2] = IZ[laser] * range;
          LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, intensity[index]);
          listeners.forEach(listener -> listener.spacial(lidarSpacialEvent));
        }
        ++index;
      }
    }
    byteBuffer.position(position + 24 * 4 + 24);
    long status = byteBuffer.getLong(); // status
    if (status != 0)
      throw new RuntimeException();
    // System.out.println(status);
  }
}
