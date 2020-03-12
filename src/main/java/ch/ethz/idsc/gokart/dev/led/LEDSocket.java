// code by gjoel
package ch.ethz.idsc.gokart.dev.led;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.gokart.core.PutListener;
import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.core.RankedPutProviders;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.led.LEDLcm;
import ch.ethz.idsc.retina.util.StartAndStoppable;

public final class LEDSocket implements StartAndStoppable {
  public static final LEDSocket INSTANCE = new LEDSocket();
  // ---
  private final RankedPutProviders<LEDPutEvent> rankedPutProviders = new RankedPutProviders<>();
  private final List<PutListener<LEDPutEvent>> putListeners = new CopyOnWriteArrayList<>();
  private final PutListener<LEDPutEvent> ledLcm = putEvent -> LEDLcm.publish(GokartLcmChannel.LED_STATUS, putEvent.status);
  private Timer timer;

  @Override // from StartAndStoppable
  public final void start() {
    addPutProvider(LEDPutFallback.INSTANCE);
    addPutListener(LEDSerialSocket.INSTANCE);
    addPutListener(ledLcm);

    timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        for (List<PutProvider<LEDPutEvent>> putProviders : rankedPutProviders.values())
          for (PutProvider<LEDPutEvent> putProvider : putProviders) {
            Optional<LEDPutEvent> optional = putProvider.putEvent();
            if (optional.isPresent())
              try {
                LEDPutEvent putEvent = optional.get();
                for (PutListener<LEDPutEvent> putListener : putListeners)
                  putListener.putEvent(putEvent); // notify put listener
                return;
              } catch (Exception exception) {
                exception.printStackTrace();
              }
          }
      }
    }, 70, 10);
  }

  @Override // from StartAndStoppable
  public final void stop() {
    if (Objects.nonNull(timer)) {
      timer.cancel();
      timer = null;
    }

    removePutListener(ledLcm);
    removePutListener(LEDSerialSocket.INSTANCE);
    removePutProvider(LEDPutFallback.INSTANCE);
  }

  public final void addPutProvider(PutProvider<LEDPutEvent> putProvider) {
    boolean added = rankedPutProviders.add(putProvider);
    if (!added) {
      System.err.println(putProvider.getClass().getSimpleName());
      new RuntimeException("put provider not added").printStackTrace();
    }
  }

  public final void removePutProvider(PutProvider<LEDPutEvent> putProvider) {
    boolean removed = rankedPutProviders.remove(putProvider);
    if (!removed) {
      System.err.println(putProvider.getClass().getSimpleName());
      new RuntimeException("put provider not removed").printStackTrace();
    }
  }

  public final void addPutListener(PutListener<LEDPutEvent> putListener) {
    boolean added = putListeners.add(putListener);
    if (!added) {
      System.err.println(putListener.getClass().getSimpleName());
      new RuntimeException("put listener not added").printStackTrace();
    }
  }

  public final void removePutListener(PutListener<LEDPutEvent> putListener) {
    boolean removed = putListeners.remove(putListener);
    if (!removed) {
      System.err.println(putListener.getClass().getSimpleName());
      new RuntimeException("put listener not removed").printStackTrace();
    }
  }
}
