// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentSkipListSet;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

/** template argument T is {@link RimoGetListener}, {@link LinmotGetListener}, ...
 * 
 * Example interface on datahaki's computer
 * 
 * enx9cebe8143edb Link encap:Ethernet HWaddr 9c:eb:e8:14:3e:db inet
 * addr:192.168.1.1 Bcast:192.168.1.255 Mask:255.255.255.0 inet6 addr:
 * fe80::9eeb:e8ff:fe14:3edb/64 Scope:Link UP BROADCAST RUNNING MULTICAST
 * MTU:1500 Metric:1 RX packets:466380 errors:0 dropped:0 overruns:0 frame:0 TX
 * packets:233412 errors:0 dropped:0 overruns:0 carrier:0 collisions:0
 * txqueuelen:1000 RX bytes:643249464 (643.2 MB) TX bytes:17275914 (17.2 MB) */
public abstract class AutoboxSocket<GE, T extends GetListener<GE>, PE, P extends PutProvider<PE>> implements //
    StartAndStoppable, ByteArrayConsumer {
  private final DatagramSocketManager datagramSocketManager;
  private final List<T> listeners = new LinkedList<>();
  private final Set<P> providers = new ConcurrentSkipListSet<>(PutProviderComparator.INSTANCE);
  private Timer timer;

  public AutoboxSocket(DatagramSocketManager datagramSocketManager) {
    this.datagramSocketManager = datagramSocketManager;
    datagramSocketManager.addListener(this);
  }

  @Override
  public final void start() {
    datagramSocketManager.start();
    timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        for (PutProvider<PE> putProvider : providers) {
          Optional<PE> optional = putProvider.getPutEvent();
          if (optional.isPresent())
            try {
              datagramSocketManager.send(getDatagramPacket(optional.get()));
              return;
            } catch (Exception exception) {
              exception.printStackTrace();
            }
        }
        System.err.println("no command provided");
      }
    }, 100, getPeriod());
  }

  protected abstract long getPeriod();

  protected abstract DatagramPacket getDatagramPacket(PE optional) throws UnknownHostException;

  protected abstract GE createGetEvent(ByteBuffer byteBuffer);

  @Override
  public final void accept(byte[] data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    GE getEvent = createGetEvent(byteBuffer);
    for (T listener : listeners)
      try {
        listener.digest(getEvent);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  @Override
  public final void stop() {
    timer.cancel();
    datagramSocketManager.stop();
  }

  public final void addProvider(P putProvider) {
    boolean added = providers.add(putProvider);
    if (!added)
      throw new RuntimeException();
  }

  public final void removeProvider(P putProvider) {
    boolean removed = providers.remove(putProvider);
    if (!removed)
      new RuntimeException().printStackTrace();
  }

  public final void addListener(T getListener) {
    listeners.add(getListener);
  }

  public final void removeListener(T getListener) {
    listeners.remove(getListener);
  }

  public final boolean hasListeners() {
    return !listeners.isEmpty();
  }
}
