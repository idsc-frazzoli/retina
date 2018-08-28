// code by jph
package ch.ethz.idsc.demo.mg;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** 1) for offline use append an instance of class as DavisDvsListener
 * to handler
 * 
 * 2) for real-time use append timer task to timer */
/* for demonstration only */ class TaskAndOfflineExample implements DavisDvsListener {
  Integer last = null;
  int period_us = 100_000;

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    // this callback should only be used in offline post-processing
    // this callback blocks the other dvs event handlers
    // and thus ensures that all the information is processed even if
    // it is slower than real-time
    if (Objects.isNull(last))
      last = davisDvsEvent.time;
    // use timestamp of event for periodic execution
    if (last + period_us < davisDvsEvent.time) {
      last += period_us;
      myComputation();
    }
  }

  public TimerTask timerTask = new TimerTask() {
    @Override
    public void run() {
      myComputation();
    }
  };

  private void myComputation() {
    // do something here
  }

  public static void main(String[] args) {
    // main function shows use in real-time system
    TaskAndOfflineExample example = new TaskAndOfflineExample();
    Timer timer = new Timer(); // <- can be shared
    timer.schedule(example.timerTask, example.period_us / 1000);
    // ---
    // timer.cancel(); // for termination, the timer needs to be cancelled at some point
  }
}
