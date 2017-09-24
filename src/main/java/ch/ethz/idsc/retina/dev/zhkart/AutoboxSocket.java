// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

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
public abstract class AutoboxSocket<GE, PE> //
    implements StartAndStoppable {
  private final DatagramSocketManager datagramSocketManager;
  private final List<GetListener<GE>> getListeners = new CopyOnWriteArrayList<>();
  // private final List<T> putListeners = new CopyOnWriteArrayList<>();
  private final ByteArrayConsumer byteArrayConsumer = new ByteArrayConsumer() {
    @Override
    public void accept(byte[] data, int length) {
      ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, length);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      GE getEvent = createGetEvent(byteBuffer);
      for (GetListener<GE> listener : getListeners)
        try {
          listener.getEvent(getEvent);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
  };
  private final Set<PutProvider<PE>> providers = new ConcurrentSkipListSet<>(PutProviderComparator.INSTANCE);
  private Timer timer;

  protected AutoboxSocket(DatagramSocketManager datagramSocketManager) {
    this.datagramSocketManager = datagramSocketManager;
    datagramSocketManager.addListener(byteArrayConsumer);
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
  public final void stop() {
    if (Objects.nonNull(timer)) {
      timer.cancel();
      timer = null;
    }
    datagramSocketManager.stop();
  }

  public final void addProvider(PutProvider<PE> putProvider) {
    boolean added = providers.add(putProvider);
    if (!added)
      throw new RuntimeException();
  }

  public final void removeProvider(PutProvider<PE> putProvider) {
    boolean removed = providers.remove(putProvider);
    if (!removed)
      new RuntimeException("provider was not listed").printStackTrace();
  }

  public final void addGetListener(GetListener<GE> getListener) {
    getListeners.add(getListener);
  }

  public final void removeGetListener(GetListener<GE> getListener) {
    boolean removed = getListeners.remove(getListener);
    if (!removed)
      new RuntimeException("listener was not listed").printStackTrace();
  }
  // public final void addPutListener(T getListener) {
  // putListeners.add(getListener);
  // }
  //
  // public final void removePutListener(T getListener) {
  // boolean removed = getListeners.remove(getListener);
  // if (!removed)
  // new RuntimeException("listener was not listed").printStackTrace();
  // }
}
