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
public abstract class AutoboxSocket<GE extends DataEvent, PE extends DataEvent> implements StartAndStoppable {
  private final DatagramSocketManager datagramSocketManager;
  private final List<GetListener<GE>> getListeners = new CopyOnWriteArrayList<>();
  private final List<PutListener<PE>> putListeners = new CopyOnWriteArrayList<>();
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
          Optional<PE> optional = putProvider.putEvent();
          if (optional.isPresent())
            try {
              PE putEvent = optional.get();
              byte[] data = putEvent.asArray();
              datagramSocketManager.send(getDatagramPacket(data));
              putListeners.forEach(listener -> listener.putEvent(putEvent));
              return;
            } catch (Exception exception) {
              exception.printStackTrace();
            }
        }
        System.err.println("no command provided");
      }
    }, 70, getPeriod());
  }

  protected abstract long getPeriod();

  protected abstract DatagramPacket getDatagramPacket(byte[] data) throws UnknownHostException;

  protected abstract GE createGetEvent(ByteBuffer byteBuffer);

  @Override
  public final void stop() {
    if (Objects.nonNull(timer)) {
      timer.cancel();
      timer = null;
    }
    datagramSocketManager.stop();
  }

  /***************************************************/
  public final void addAll(Object object) {
    if (object instanceof PutProvider) {
      @SuppressWarnings("unchecked")
      PutProvider<PE> putProvider = (PutProvider<PE>) object;
      addPutProvider(putProvider);
    }
    if (object instanceof GetListener) {
      @SuppressWarnings("unchecked")
      GetListener<GE> getListener = (GetListener<GE>) object;
      addGetListener(getListener);
    }
    if (object instanceof PutListener) {
      @SuppressWarnings("unchecked")
      PutListener<PE> putListener = (PutListener<PE>) object;
      addPutListener(putListener);
    }
  }

  public final void removeAll(Object object) {
    if (object instanceof PutProvider) {
      @SuppressWarnings("unchecked")
      PutProvider<PE> putProvider = (PutProvider<PE>) object;
      removePutProvider(putProvider);
    }
    if (object instanceof GetListener) {
      @SuppressWarnings("unchecked")
      GetListener<GE> getListener = (GetListener<GE>) object;
      removeGetListener(getListener);
    }
    if (object instanceof PutListener) {
      @SuppressWarnings("unchecked")
      PutListener<PE> putListener = (PutListener<PE>) object;
      removePutListener(putListener);
    }
  }

  /***************************************************/
  public final void addPutProvider(PutProvider<PE> putProvider) {
    boolean added = providers.add(putProvider);
    if (!added)
      throw new RuntimeException();
  }

  public final void removePutProvider(PutProvider<PE> putProvider) {
    boolean removed = providers.remove(putProvider);
    if (!removed)
      new RuntimeException("provider was not listed").printStackTrace();
  }

  /***************************************************/
  public final void addGetListener(GetListener<GE> getListener) {
    getListeners.add(getListener);
  }

  public final void removeGetListener(GetListener<GE> getListener) {
    boolean removed = getListeners.remove(getListener);
    if (!removed)
      new RuntimeException("listener was not listed").printStackTrace();
  }

  /***************************************************/
  public final void addPutListener(PutListener<PE> putListener) {
    putListeners.add(putListener);
  }

  public final void removePutListener(PutListener<PE> putListener) {
    boolean removed = putListeners.remove(putListener);
    if (!removed)
      new RuntimeException("listener was not listed").printStackTrace();
  }
}
