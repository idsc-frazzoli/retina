// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.core.ColumnTimedImageListener;
import ch.ethz.idsc.retina.core.TimedImageListener;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.util.io.UserHome;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

// TODO redraw thread is independent of sync signal of images...!
public class DavisViewerFrame implements TimedImageListener, ColumnTimedImageListener, DavisImuFrameListener {
  private final JFrame jFrame = new JFrame();
  private DavisEventStatistics davisEventStatistics;
  private Tensor eventCount = Array.zeros(3);
  private final Timer timer = new Timer();
  private final DavisViewerComponent davisDefaultComponent = new DavisViewerComponent();

  public DavisViewerFrame(DavisDevice davisDevice) {
    jFrame.setBounds(100, 100, 730, 300);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    Component component = jFrame.getContentPane();
    JPanel jPanel = (JPanel) component;
    {
      JToolBar jToolBar = new JToolBar();
      jToolBar.setFloatable(false);
      {
        JButton jButton = new JButton("exp");
        jButton.addActionListener(actionEvent -> {
          System.out.println("here");
          try {
            ImageIO.write(davisDefaultComponent.apsImage, "png", UserHome.Pictures("apsimage.png"));
          } catch (Exception exception) {
            // ---
          }
        });
        jToolBar.add(jButton);
      }
      jPanel.add(jToolBar, BorderLayout.NORTH);
    }
    jPanel.add(davisDefaultComponent.jComponent, BorderLayout.CENTER);
    // jFrame.setContentPane(davisDefaultComponent.jComponent);
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
          davisDefaultComponent.jComponent.repaint();
        }
      };
      timer.schedule(timerTask, 100, 33); // 33 ms -> 30 Hz
    }
    {
      TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          if (Objects.nonNull(davisEventStatistics)) {
            davisDefaultComponent.displayEventCount = davisEventStatistics.eventCount().subtract(eventCount);
            eventCount = davisEventStatistics.eventCount();
          }
        }
      };
      timer.schedule(timerTask, 100, 1000); // 33 ms -> 30 Hz
    }
  }

  public void close() {
    timer.cancel();
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  @Override // from DavisImuFrameListener
  public void imuFrame(DavisImuFrame davisImuFrame) {
    davisDefaultComponent.imuFrame = davisImuFrame;
  }

  @Override // from TimedImageListener
  public void image(int time, BufferedImage bufferedImage) {
    davisDefaultComponent.setDvsImage(bufferedImage);
  }

  @Override // from ColumnTimedImageListener
  public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
    if (!isComplete)
      System.err.println("image incomplete");
    davisDefaultComponent.apsImage = bufferedImage;
    davisDefaultComponent.isComplete = isComplete;
  }

  public ColumnTimedImageListener rstListener = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      if (!isComplete)
        System.err.println("rst incomplete");
      davisDefaultComponent.rstImage = bufferedImage;
      davisDefaultComponent.isComplete = isComplete;
    }
  };

  public void setStatistics(DavisEventStatistics davisEventStatistics) {
    this.davisEventStatistics = davisEventStatistics;
  }
}
