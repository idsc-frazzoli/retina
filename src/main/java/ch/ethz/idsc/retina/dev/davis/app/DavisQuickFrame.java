// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;

// TODO redraw thread is independent of sync signal of images...!
public class DavisQuickFrame {
  private final JFrame jFrame = new JFrame();
  private final Timer timer = new Timer();
  public final DavisQuickComponent davisViewerComponent = new DavisQuickComponent();

  public DavisQuickFrame(DavisDevice davisDevice) {
    int width = davisDevice.getWidth() * 2;
    int height = davisDevice.getHeight() * 4;
    jFrame.setBounds(100, 100, width, height + 10);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    Component component = jFrame.getContentPane();
    JPanel jPanel = (JPanel) component;
    jPanel.add(davisViewerComponent.jComponent, BorderLayout.CENTER);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        close();
      }
    });
    jFrame.setVisible(true);
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

  public void close() {
    timer.cancel();
    jFrame.setVisible(false);
    jFrame.dispose();
  }
}
