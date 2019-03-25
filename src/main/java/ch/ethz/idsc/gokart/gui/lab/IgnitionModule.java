// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class IgnitionModule extends AutoboxAbstractModule {
  private final AutoboxCompactComponent autoboxCompactComponent = new AutoboxCompactComponent();
  private final AutoboxIntrospectionComponent autoboxIntrospectionComponent = new AutoboxIntrospectionComponent();

  @Override
  protected void protected_first(Timer timer, JFrame jFrame) {
    autoboxCompactComponent.start();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        autoboxCompactComponent.update();
        autoboxIntrospectionComponent.update();
      }
    }, 100, 100);
    // ---
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.add(autoboxCompactComponent.getScrollPane(), BorderLayout.CENTER);
    autoboxIntrospectionComponent.jPanel.setPreferredSize(new Dimension(200, 4 * 36));
    jPanel.add(autoboxIntrospectionComponent.jPanel, BorderLayout.SOUTH);
    jFrame.setContentPane(jPanel);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        autoboxCompactComponent.stop();
        timer.cancel();
      }
    });
  }

  public static void main(String[] args) throws Exception {
    standalone(new IgnitionModule());
  }
}
