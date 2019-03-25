// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class AutoboxIntrospectionModule extends AutoboxAbstractModule {
  private static final int PERIOD_MS = 200; // 200 ms -> 5 Hz
  // ---
  private final AutoboxIntrospectionComponent autoboxIntrospectionComponent = new AutoboxIntrospectionComponent();

  @Override // from AbstractModule
  protected void protected_first(Timer timer, JFrame jFrame) {
    jFrame.setContentPane(autoboxIntrospectionComponent.jPanel);
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        autoboxIntrospectionComponent.update();
      }
    };
    timer.schedule(timerTask, 100, PERIOD_MS);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        timer.cancel();
      }
    });
  }

  public static void main(String[] args) throws Exception {
    standalone(new AutoboxIntrospectionModule());
  }
}
