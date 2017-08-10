// code by jph
package ch.ethz.idsc.retina.davis.app;

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

// TODO redraw thread is independent of sync signal of images...!
public class DavisDefaultDisplay {
  private final JLabel jLabel = new JLabel();
  private BufferedImage bufferedImage = null;
  private BufferedImage dvsImage = null;
  private final JFrame jFrame = new JFrame();
  private long repaint_tic = System.nanoTime();
  private boolean isComplete;
  public final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      repaint_tic = System.nanoTime();
      if (Objects.nonNull(bufferedImage))
      // synchronized (bufferedImage)
      {
        graphics.drawImage(bufferedImage, 0, 0, jLabel);
        if (!isComplete)
          graphics.drawString("incomplete!", 0, 200);
      }
      if (Objects.nonNull(dvsImage))
        graphics.drawImage(dvsImage, 240, 0, jLabel);
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
    timer.schedule(timerTask, 100, 50);
  }

  public final ColumnTimedImageListener apsRenderer = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      if (!isComplete)
        System.err.println("image incomplete");
      setBufferedImage(bufferedImage, isComplete);
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

  private void setBufferedImage(BufferedImage bufferedImage, boolean isComplete) {
    // if (Objects.isNull(this.bufferedImage)) {
    this.bufferedImage = bufferedImage;
    this.isComplete = isComplete;
    // } else
    // synchronized (this.bufferedImage) {
    // this.bufferedImage = bufferedImage;
    // this.isComplete = isComplete;
    // }
  }
}
