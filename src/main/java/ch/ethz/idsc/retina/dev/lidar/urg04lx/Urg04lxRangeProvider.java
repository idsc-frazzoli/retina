// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;

public class Urg04lxRangeProvider implements LidarRayDataListener {
  private static final double MILLIMETER_TO_METER = 0.001;
  // ---
  private final List<Urg04lxRangeListener> listeners = new LinkedList<>();

  public void addListener(Urg04lxRangeListener urg04lxRangeListener) {
    listeners.add(urg04lxRangeListener);
  }

  private int usec;

  @Override
  public void timestamp(int usec, int type) {
    this.usec = usec;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    double[] range = new double[Urg04lxDevice.MAX_POINTS];
    for (int index = 0; index < Urg04lxDevice.MAX_POINTS; ++index)
      range[index] = (byteBuffer.getShort() & 0xffff) * MILLIMETER_TO_METER;
    Urg04lxRangeEvent urg04lxEvent = new Urg04lxRangeEvent(usec, range);
    listeners.forEach(listener -> listener.range(urg04lxEvent));
  }
}
