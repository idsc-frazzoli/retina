// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.core.ColumnTimedImageListener;
import ch.ethz.idsc.retina.core.TimedImageListener;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.davis.io.imu.DavisImuFrame;
import ch.ethz.idsc.retina.davis.io.imu.DavisImuFrameListener;
import ch.ethz.idsc.retina.util.Stopwatch;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Round;

// TODO redraw thread is independent of sync signal of images...!
public class DavisDefaultDisplay implements TimedImageListener, ColumnTimedImageListener, DavisImuFrameListener {
  private final JLabel jLabel = new JLabel();
  private BufferedImage apsImage = null;
  private BufferedImage dvsImage = null;
  private DavisImuFrame imuFrame = null;
  private final JFrame jFrame = new JFrame();
  private final Stopwatch stopwatch = new Stopwatch();
  private boolean isComplete;
  private DavisEventStatistics davisEventStatistics;
  private Tensor eventCount = Array.zeros(3);
  private Tensor displayEventCount = Array.zeros(3);
  public final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      long period = stopwatch.stop();
      stopwatch.start();
      if (Objects.nonNull(apsImage)) {
        graphics.drawImage(apsImage, 0, 0, jLabel);
        if (!isComplete)
          graphics.drawString("incomplete!", 0, 200);
      }
      if (Objects.nonNull(dvsImage))
        graphics.drawImage(dvsImage, 240, 0, jLabel);
      if (Objects.nonNull(imuFrame)) {
        graphics.setColor(Color.GRAY);
        graphics.drawString( //
            String.format("%4.1f C", imuFrame.temperature), 70, 190);
        graphics.drawString( //
            imuFrame.accel().map(Round._2).toString(), 120, 190);
        graphics.drawString( //
            imuFrame.gyro().map(Round._2).toString(), 260, 190);
      }
      // if (Objects.nonNull(davisEventStatistics))
      {
        graphics.setColor(Color.GRAY);
        graphics.drawString(displayEventCount.toString(), 0, 200);
      }
      // ---
      graphics.setColor(Color.GRAY);
      graphics.drawString(String.format("%4.1f Hz", (1.0e9 / period)), 0, 190);
    }
  };
  private final Timer timer = new Timer();

  public DavisDefaultDisplay() {
    jFrame.setBounds(100, 100, 500, 200);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jComponent);
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
          jComponent.repaint();
        }
      };
      timer.schedule(timerTask, 100, 33); // 33 ms -> 30 Hz
    }
    {
      TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          if (Objects.nonNull(davisEventStatistics)) {
            displayEventCount = davisEventStatistics.eventCount().subtract(eventCount);
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

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    this.imuFrame = davisImuFrame;
  }

  @Override
  public void image(int time, BufferedImage bufferedImage) {
    this.dvsImage = bufferedImage;
  }

  @Override
  public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
    if (!isComplete)
      System.err.println("image incomplete");
    this.apsImage = bufferedImage;
    this.isComplete = isComplete;
  }

  public void setStatistics(DavisEventStatistics davisEventStatistics) {
    this.davisEventStatistics = davisEventStatistics;
  }
}
