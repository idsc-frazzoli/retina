// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public abstract class AutoboxSocket<T> implements StartAndStoppable, ByteArrayConsumer {
  protected final List<T> listeners = new LinkedList<>();
  protected final DatagramSocketManager datagramSocketManager;

  public AutoboxSocket(DatagramSocketManager datagramSocketManager) {
    this.datagramSocketManager = datagramSocketManager;
    datagramSocketManager.addListener(this);
  }

  public final void addListener(T rimoGetListener) {
    listeners.add(rimoGetListener);
  }

  public final void removeListener(T rimoGetListener) {
    listeners.remove(rimoGetListener);
  }

  public final boolean hasListeners() {
    return !listeners.isEmpty();
  }

  @Override
  public final void start() {
    datagramSocketManager.start();
  }

  @Override
  public final void stop() {
    datagramSocketManager.stop();
  }
}
