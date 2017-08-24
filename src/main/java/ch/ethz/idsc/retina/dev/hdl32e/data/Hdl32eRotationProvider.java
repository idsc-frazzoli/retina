// code by jph
package ch.ethz.idsc.retina.dev.hdl32e.data;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringDataListener;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRotationEvent;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRotationEventListener;

/** converts firing data to spacial events with time, 3d-coordinates and intensity */
public class Hdl32eRotationProvider implements Hdl32eFiringDataListener {
  private final List<Hdl32eRotationEventListener> listeners = new LinkedList<>();
  private int usec;
  private int rotational_last = -1;

  public void addListener(Hdl32eRotationEventListener listener) {
    listeners.add(listener);
  }

  @Override
  public void timestamp(int usec, byte type, byte value) {
    this.usec = usec;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (rotational < rotational_last) {
      Hdl32eRotationEvent hdl32eRotationEvent = new Hdl32eRotationEvent(usec, rotational);
      listeners.forEach(listener -> listener.rotation(hdl32eRotationEvent));
    }
    rotational_last = rotational;
  }
}
