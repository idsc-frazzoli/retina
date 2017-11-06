// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.owly.demo.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

// TODO redraw thread is independent of sync signal of images...!
public class DavisViewerFrame {
  public final JFrame jFrame = new JFrame();
  @SuppressWarnings("unused")
  private DavisEventStatistics davisEventStatistics;
  // private Tensor eventCount = Array.zeros(3);
  private final Timer timer = new Timer();
  public final DavisViewerComponent davisViewerComponent = new DavisViewerComponent();
  public final DavisTallyProvider davisTallyProvider = new DavisTallyProvider( //
      davisTallyEvent -> davisViewerComponent.davisTallyEvent = davisTallyEvent);

  public DavisViewerFrame(DavisDevice davisDevice) {
    jFrame.setBounds(100, 100, 730, 500);
    Component component = jFrame.getContentPane();
    JPanel jPanel = (JPanel) component;
    {
      JToolBar jToolBar = new JToolBar();
      jToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
      jToolBar.setFloatable(false);
      {
        JButton jButton = new JButton("exp");
        jButton.addActionListener(actionEvent -> {
          System.out.println("here");
          try {
            ImageIO.write(davisViewerComponent.sigImage, "png", UserHome.Pictures("sigImage.png"));
          } catch (Exception exception) {
            // ---
          }
        });
        jToolBar.add(jButton);
      }
      {
        SpinnerLabel<Integer> sl = new SpinnerLabel<>();
        sl.addSpinnerListener(shift -> davisTallyProvider.setShift(shift));
        sl.setList(Arrays.asList(6, 7, 8, 9));
        sl.setValueSafe(davisTallyProvider.getShift());
        sl.addToComponentReduced(jToolBar, new Dimension(70, 28), "shift");
      }
      jPanel.add(jToolBar, BorderLayout.NORTH);
    }
    jPanel.add(davisViewerComponent.jComponent, BorderLayout.CENTER);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        timer.cancel();
      }
    });
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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

  public void setStatistics(DavisEventStatistics davisEventStatistics) {
    this.davisEventStatistics = davisEventStatistics;
  }
}
