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

import ch.ethz.idsc.retina.davis.ColumnTimedImageListener;
import ch.ethz.idsc.retina.davis.TimedImageListener;
import ch.ethz.idsc.retina.util.Stopwatch;

// TODO redraw thread is independent of sync signal of images...!
public class DavisDefaultDisplay {
  private final JLabel jLabel = new JLabel();
  private BufferedImage apsImage = null;
  private BufferedImage dvsImage = null;
  private final JFrame jFrame = new JFrame();
  private final Stopwatch stopwatch = new Stopwatch();
  private boolean isComplete;
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
      // TODO draw imu
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
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        jComponent.repaint();
      }
    };
    timer.schedule(timerTask, 100, 33); // 33 ms -> 30 Hz
  }

  public final ColumnTimedImageListener apsRenderer = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      if (!isComplete)
        System.err.println("image incomplete");
      setApsImage(bufferedImage, isComplete);
    }
  };
  public final TimedImageListener dvsRenderer = new TimedImageListener() {
    @Override
    public void image(int time, BufferedImage bufferedImage) {
      setDvsImage(bufferedImage);
    }
  };

  public void close() {
    timer.cancel();
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  void setDvsImage(BufferedImage bufferedImage) {
    this.dvsImage = bufferedImage;
  }

  private void setApsImage(BufferedImage bufferedImage, boolean isComplete) {
    this.apsImage = bufferedImage;
    this.isComplete = isComplete;
  }
}
