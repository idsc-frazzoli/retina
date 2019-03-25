// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.util.Timer;
import java.util.TimerTask;

import junit.framework.TestCase;

public class AutoboxCompactComponentTest extends TestCase {
  public void testStartStop() throws InterruptedException {
    AutoboxCompactComponent autoboxCompactComponent = new AutoboxCompactComponent();
    autoboxCompactComponent.start();
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        autoboxCompactComponent.update();
      }
    }, 100, 50);
    Thread.sleep(100);
    autoboxCompactComponent.stop();
    timer.cancel();
  }
}
