// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

/** template argument T is {@link RimoGetListener}, {@link LinmotGetListener}, ... */
public abstract class AutoboxSocket<T> implements StartAndStoppable, ByteArrayConsumer {
  protected final DatagramSocketManager datagramSocketManager;
  protected final List<T> listeners = new LinkedList<>();

  public AutoboxSocket(DatagramSocketManager datagramSocketManager) {
    this.datagramSocketManager = datagramSocketManager;
    datagramSocketManager.addListener(this);
  }

  @Override
  public final void start() {
    datagramSocketManager.start();
  }

  @Override
  public final void stop() {
    datagramSocketManager.stop();
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
}
