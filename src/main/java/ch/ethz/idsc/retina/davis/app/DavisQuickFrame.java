// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.davis.DavisDevice;

// TODO redraw thread is independent of sync signal of images...!
public class DavisQuickFrame {
  public final JFrame jFrame = new JFrame();
  private final Timer timer = new Timer();
  public final DavisQuickComponent davisViewerComponent;

  public DavisQuickFrame(DavisDevice davisDevice, DavisQuickComponent davisViewerComponent) {
    this.davisViewerComponent = davisViewerComponent;
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    Component component = jFrame.getContentPane();
    JPanel jPanel = (JPanel) component;
    jPanel.add(davisViewerComponent.jComponent, BorderLayout.CENTER);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        timer.cancel();
        System.out.println("timer cancel");
      }
    });
    {
      TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          davisViewerComponent.jComponent.repaint();
        }
      };
      timer.schedule(timerTask, 100, 33); // 33 ms -> 30 Hz
    }
  }
}
