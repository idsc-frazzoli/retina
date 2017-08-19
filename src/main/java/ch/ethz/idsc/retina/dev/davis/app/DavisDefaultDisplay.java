// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.core.ColumnTimedImageListener;
import ch.ethz.idsc.retina.core.TimedImageListener;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

// TODO redraw thread is independent of sync signal of images...!
public class DavisDefaultDisplay implements TimedImageListener, ColumnTimedImageListener, DavisImuFrameListener {
  private final JFrame jFrame = new JFrame();
  private DavisEventStatistics davisEventStatistics;
  private Tensor eventCount = Array.zeros(3);
  private final Timer timer = new Timer();
  private final int width;
  private final int height;
  private final BufferedImage bufferedImage;
  private final byte[] bytes;
  DavisDefaultComponent davisDefaultComponent = new DavisDefaultComponent();

  public DavisDefaultDisplay(DavisDevice davisDevice) {
    width = davisDevice.getWidth();
    height = davisDevice.getHeight();
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    jFrame.setBounds(100, 100, 500, 200);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(davisDefaultComponent.jComponent);
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

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    davisDefaultComponent.imuFrame = davisImuFrame;
  }

  @Override
  public void image(int time, BufferedImage bufferedImage) {
    // DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    // byte[] data = dataBufferByte.getData();
    // synchronized (bufferedImage) {
    // ByteBuffer.wrap(bytes).put(data);
    // }
    davisDefaultComponent.dvsImage = bufferedImage;
  }

  @Override
  public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
    if (!isComplete)
      System.err.println("image incomplete");
    davisDefaultComponent.apsImage = bufferedImage;
    davisDefaultComponent.isComplete = isComplete;
  }

  public void setStatistics(DavisEventStatistics davisEventStatistics) {
    this.davisEventStatistics = davisEventStatistics;
  }
}
