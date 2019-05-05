// code by jph
package ch.ethz.idsc.retina.util.time;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class ScheduledExecutorServiceDemo {
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public void beepForAnHour() {
    final Runnable beeper = new Runnable() {
      @Override
      public void run() {
        System.out.println("beg");
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("end");
      }
    };
    final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 0, 550, TimeUnit.MILLISECONDS);
    scheduler.schedule(() -> beeperHandle.cancel(true), 1, TimeUnit.SECONDS);
    while (!beeperHandle.isDone()) {
      // ---
    }
    scheduler.shutdownNow();
  }

  public static void main(String[] args) {
    new ScheduledExecutorServiceDemo().beepForAnHour();
  }
}