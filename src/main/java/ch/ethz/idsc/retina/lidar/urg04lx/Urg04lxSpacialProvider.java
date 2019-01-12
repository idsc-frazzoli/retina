// code by jph
package ch.ethz.idsc.retina.lidar.urg04lx;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

public class Urg04lxSpacialProvider implements LidarSpacialProvider {
  /* package */ static final double TO_METER = 0.001; // [mm] to [m]
  private static final float TO_METER_FLOAT = (float) TO_METER;
  /** recommended threshold points closer than 2[cm] == 0.02[m] are discarded */
  public static final double THRESHOLD = 0.02; // [m]
  // ---
  private final List<LidarSpacialListener> listeners = new LinkedList<>();
  // ---
  private final int dimensions;
  private final float[] dirx;
  private final float[] diry;
  /* package for testing */ int limit_lo;

  public Urg04lxSpacialProvider(int dimensions) {
    this.dimensions = dimensions;
    /** p.2 Detection Area: 240 [deg] */
    Tensor angle = Subdivide.of( //
        Urg04lxDevice.FOV_LO * Math.PI / 180, //
        Urg04lxDevice.FOV_HI * Math.PI / 180, //
        Urg04lxDevice.MAX_POINTS - 1);
    dirx = Primitives.toFloatArray(Cos.of(angle));
    diry = Primitives.toFloatArray(Sin.of(angle));
    setLimitLo(THRESHOLD);
  }

  public void setLimitLo(double closest) {
    limit_lo = (int) (closest / TO_METER);
  }

  @Override // from LidarSpacialProvider
  public void addListener(LidarSpacialListener lidarSpacialListener) {
    listeners.add(lidarSpacialListener);
  }

  private int usec;

  @Override // from LidarRayDataListener
  public void timestamp(int usec, int type) {
    this.usec = usec;
  }

  @Override // from LidarRayDataListener
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // for the urg04lxug01 sensor the rotational parameter is irrelevant
    float[] coords = new float[dimensions];
    for (int index = 0; index < Urg04lxDevice.MAX_POINTS; ++index) {
      int distance = byteBuffer.getShort() & 0xffff;
      if (limit_lo <= distance) {
        float dist_m = distance * TO_METER_FLOAT;
        coords[0] = dirx[index] * dist_m;
        coords[1] = diry[index] * dist_m;
        LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, (byte) 255);
        listeners.forEach(listener -> listener.lidarSpacial(lidarSpacialEvent));
      }
    }
  }
}
