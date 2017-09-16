// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEventListener;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

public class Urg04lxSpacialProvider implements LidarSpacialProvider {
  private static final double TO_METER = 0.001; // [mm] to [m]
  private static final float TO_METER_FLOAT = (float) TO_METER;
  /** recommended threshold
   * points closer than 2[cm] == 0.02[m] are discarded */
  public static final double THRESHOLD = 0.02; // [m]
  // ---
  private final List<LidarSpacialEventListener> listeners = new LinkedList<>();
  // ---
  private final int dimensions;
  private final float[] DIRX;
  private final float[] DIRY;
  /* package for testing */ int limit_lo;

  public Urg04lxSpacialProvider(int dimensions) {
    this.dimensions = dimensions;
    /** p.2 Detection Area: 240 [deg] */
    Tensor angle = Subdivide.of( //
        Urg04lxDevice.FOV_LO * Math.PI / 180, //
        Urg04lxDevice.FOV_HI * Math.PI / 180, //
        Urg04lxDevice.MAX_POINTS - 1).unmodifiable();
    DIRX = Primitives.toArrayFloat(Cos.of(angle));
    DIRY = Primitives.toArrayFloat(Sin.of(angle));
    setLimitLo(THRESHOLD);
  }

  public void setLimitLo(double closest) {
    limit_lo = (int) (closest / TO_METER);
  }

  @Override
  public void addListener(LidarSpacialEventListener lidarSpacialEventListener) {
    listeners.add(lidarSpacialEventListener);
  }

  private int usec;

  @Override
  public void timestamp(int usec, int type) {
    this.usec = usec;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // for the urg04lxug01 sensor the rotational parameter is irrelevant
    float[] coords = new float[dimensions];
    for (int index = 0; index < Urg04lxDevice.MAX_POINTS; ++index) {
      int distance = byteBuffer.getShort() & 0xffff;
      if (limit_lo <= distance) {
        float dist_m = distance * TO_METER_FLOAT;
        coords[0] = DIRX[index] * dist_m;
        coords[1] = DIRY[index] * dist_m;
        LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, 0xff);
        listeners.forEach(listener -> listener.spacial(lidarSpacialEvent));
      }
    }
  }
}
