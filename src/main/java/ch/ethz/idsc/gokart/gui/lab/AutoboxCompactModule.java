// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

/** AutoboxCompactModule facilitates the initialization of the actuators and the
 * monitoring of the manual control providers and sensors vital for the operation
 * 
 * module first tested on 20180427 */
public class AutoboxCompactModule extends AutoboxAbstractModule {
  private final AutoboxCompactComponent autoboxCompactComponent = new AutoboxCompactComponent();

  @Override // from AbstractModule
  protected void protected_first(Timer timer, JFrame jFrame) {
    autoboxCompactComponent.start();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        autoboxCompactComponent.update();
      }
    }, 100, 50);
    // ---
    jFrame.setContentPane(autoboxCompactComponent.getScrollPane());
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        autoboxCompactComponent.stop();
        timer.cancel();
      }
    });
  }

  public static void main(String[] args) throws Exception {
    standalone(new AutoboxCompactModule());
  }
}
