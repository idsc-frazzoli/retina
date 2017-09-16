// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;

public class Urg04lxEventProvider implements LidarRayDataListener {
  private static final double MILLIMETER_TO_METER = 0.001;
  // ---
  private final List<Urg04lxEventListener> listeners = new LinkedList<>();

  public void addListener(Urg04lxEventListener urg04lxEventListener) {
    listeners.add(urg04lxEventListener);
  }

  private int usec;

  @Override
  public void timestamp(int usec, int type) {
    // ---
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    double[] range = new double[Urg04lxDevice.MAX_POINTS];
    for (int index = 0; index < Urg04lxDevice.MAX_POINTS; ++index)
      range[index] = (byteBuffer.getShort() & 0xffff) * MILLIMETER_TO_METER;
    Urg04lxEvent urg04lxEvent = new Urg04lxEvent(usec, range);
    listeners.forEach(listener -> listener.range(urg04lxEvent));
  }
}
