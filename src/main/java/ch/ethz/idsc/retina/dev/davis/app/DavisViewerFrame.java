// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.util.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.retina.util.io.UserHome;

// TODO redraw thread is independent of sync signal of images...!
public class DavisViewerFrame implements TimedImageListener, ColumnTimedImageListener, DavisImuFrameListener {
  private final JFrame jFrame = new JFrame();
  private DavisEventStatistics davisEventStatistics;
  // private Tensor eventCount = Array.zeros(3);
  private final Timer timer = new Timer();
  private final DavisViewerComponent davisViewerComponent = new DavisViewerComponent();
  public final DavisTallyProvider davisTallyProvider = new DavisTallyProvider( //
      davisTallyEvent -> davisViewerComponent.davisTallyEvent = davisTallyEvent);

  public DavisViewerFrame(DavisDevice davisDevice) {
    jFrame.setBounds(100, 100, 730, 400);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
          davisViewerComponent.jComponent.repaint();
        }
      };
      timer.schedule(timerTask, 100, 33); // 33 ms -> 30 Hz
    }
    {
      TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          if (Objects.nonNull(davisEventStatistics)) {
            // davisDefaultComponent.displayEventCount =
            // davisEventStatistics.eventCount().subtract(eventCount);
            // eventCount = davisEventStatistics.eventCount();
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
    davisViewerComponent.imuFrame = davisImuFrame;
  }

  @Override // from TimedImageListener
  public void image(int time, BufferedImage bufferedImage) {
    davisViewerComponent.setDvsImage(bufferedImage);
  }

  @Override // from ColumnTimedImageListener
  public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
    if (!isComplete)
      System.err.println("image incomplete");
    davisViewerComponent.sigImage = bufferedImage;
    davisViewerComponent.isComplete = isComplete;
    davisViewerComponent.frame_duration = time[time.length - 1] - time[0];
  }

  public ColumnTimedImageListener rstListener = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      if (!isComplete)
        System.err.println("rst incomplete");
      davisViewerComponent.rstImage = bufferedImage;
      davisViewerComponent.isComplete = isComplete;
      davisViewerComponent.reset_duration = time[time.length - 1] - time[0];
    }
  };

  public void setStatistics(DavisEventStatistics davisEventStatistics) {
    this.davisEventStatistics = davisEventStatistics;
  }
}
