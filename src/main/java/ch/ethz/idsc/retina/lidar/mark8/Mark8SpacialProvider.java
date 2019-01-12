// code by jph
package ch.ethz.idsc.retina.lidar.mark8;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;

public class Mark8SpacialProvider implements LidarSpacialProvider {
  private static final int LASERS = 8;
  private static final float[] IR = new float[8];
  private static final float[] IZ = new float[8];
  private static final double TO_METER = 0.002;
  private static final float TO_METER_FLOAT = (float) TO_METER;
  /** angles taken from p.33
   * the values state in the data sheet are not evenly spaced...
   * need confirmation through experiments */
  private static final double[] M8_VERTICAL_ANGLES = { //
      -0.318505, -0.2692, -0.218009, -0.165195, -0.111003, -0.0557982, 0.0, 0.0557982 };
  private static final AngleVectorLookupFloat TRIGONOMETRY = new AngleVectorLookupFloat(10400, false, 0);
  // ---
  private final List<LidarSpacialListener> listeners = new LinkedList<>();
  private int usec;
  // private final byte[] intensity = new byte[24];
  private int returns;

  public Mark8SpacialProvider() {
    for (int laser = 0; laser < LASERS; ++laser) {
      double theta = M8_VERTICAL_ANGLES[laser];
      IR[laser] = (float) Math.cos(theta);
      IZ[laser] = (float) Math.sin(theta);
    }
  }

  @Override
  public void addListener(LidarSpacialListener lidarSpacialEventListener) {
    listeners.add(lidarSpacialEventListener);
  }

  @Override
  public void timestamp(int usec, int type) {
    this.usec = usec;
    returns = type;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    float dx = TRIGONOMETRY.dx(rotational);
    float dy = TRIGONOMETRY.dy(rotational);
    float[] coords = new float[3];
    // apparently the correct nesting of loops (test more)
    for (int layer = 0; layer < returns; ++layer) {
      for (int laser = 0; laser < 8; ++laser) {
        // 0 indicates an invalid point
        int distance = byteBuffer.getShort() & 0xffff;
        byte intensity = byteBuffer.get();
        if (distance != 0) {
          float range = distance * TO_METER_FLOAT; // convert to [m]
          coords[0] = IR[laser] * range * dx;
          coords[1] = IR[laser] * range * dy;
          coords[2] = IZ[laser] * range;
          LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, intensity);
          listeners.forEach(listener -> listener.lidarSpacial(lidarSpacialEvent));
        }
      }
    }
  }
}
