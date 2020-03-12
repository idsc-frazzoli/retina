// code by jph, gjoel
package ch.ethz.idsc.demo.jg.bumblebee.serial;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import ch.ethz.idsc.gokart.core.GetListener;
import ch.ethz.idsc.gokart.core.PutListener;
import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.core.RankedPutProviders;
import ch.ethz.idsc.retina.util.data.DataEvent;

public abstract class BBSerialSocket<GE extends DataEvent, PE extends DataEvent> extends SerialStringSocket {
  private static final int BAUD_RATE = 2000000;

  private final List<GetListener<GE>> getListeners = new CopyOnWriteArrayList<>();
  private final RankedPutProviders<PE> rankedPutProviders = new RankedPutProviders<>();
  private final List<PutListener<PE>> putListeners = new CopyOnWriteArrayList<>();

  protected BBSerialSocket(String port) {
    super(port, BAUD_RATE);
  }

  @Override // from SerialStringSocket
  protected final void loop() {
    for (List<PutProvider<PE>> putProviders : rankedPutProviders.values())
      for (PutProvider<PE> putProvider : putProviders) {
        Optional<PE> optional = putProvider.putEvent();
        if (optional.isPresent()) {
          PE putEvent = optional.get();
          try {
            writeln(putMessage(putEvent));

            for (PutListener<PE> putListener : putListeners)
              putListener.putEvent(putEvent); // notify put listener
            return;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    System.err.println("no command provided in " + getClass().getSimpleName());
  }

  @Override // from SerialStringSocket
  protected synchronized final void receive(String... words) {
    // FIXME gets interrupted
    System.out.println("checkpoint 0");
    Arrays.stream(words).map(Integer::parseInt).forEach(System.out::println);
    System.out.println("checkpoint 1");
    GE getEvent = createGetEvent(Arrays.stream(words).map(Integer::parseInt).collect(Collectors.toList()));
    System.out.println("checkpoint 2");
    synchronized (getListeners) {
      for (GetListener<GE> listener : getListeners)
        try {
          System.out.println("checkpoint 3");
          listener.getEvent(getEvent); // notify get listener
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
  }

  protected abstract String putMessage(PE putEvent);

  protected abstract GE createGetEvent(Collection<Integer> values);

  /***************************************************/
  public final void addPutProvider(PutProvider<PE> putProvider) {
    boolean added = rankedPutProviders.add(putProvider);
    if (!added) {
      System.err.println(putProvider.getClass().getSimpleName());
      new RuntimeException("put provider not added").printStackTrace();
    }
  }

  public final void removePutProvider(PutProvider<PE> putProvider) {
    boolean removed = rankedPutProviders.remove(putProvider);
    if (!removed) {
      System.err.println(putProvider.getClass().getSimpleName());
      new RuntimeException("put provider not removed").printStackTrace();
    }
  }

  /***************************************************/
  public final void addGetListener(GetListener<GE> getListener) {
    synchronized (getListeners) {
      boolean added = getListeners.add(getListener);
      if (!added) {
        System.err.println(getListener.getClass().getSimpleName());
        new RuntimeException("get listener not added").printStackTrace();
      }
    }
  }

  public final void removeGetListener(GetListener<GE> getListener) {
    synchronized (getListeners) {
      boolean removed = getListeners.remove(getListener);
      if (!removed) {
        System.err.println(getListener.getClass().getSimpleName());
        new RuntimeException("get listener not removed").printStackTrace();
      }
    }
  }

  /***************************************************/
  public final void addPutListener(PutListener<PE> putListener) {
    boolean added = putListeners.add(putListener);
    if (!added) {
      System.err.println(putListener.getClass().getSimpleName());
      new RuntimeException("put listener not added").printStackTrace();
    }
  }

  public final void removePutListener(PutListener<PE> putListener) {
    boolean removed = putListeners.remove(putListener);
    if (!removed) {
      System.err.println(putListener.getClass().getSimpleName());
      new RuntimeException("put listener not removed").printStackTrace();
    }
  }
}
