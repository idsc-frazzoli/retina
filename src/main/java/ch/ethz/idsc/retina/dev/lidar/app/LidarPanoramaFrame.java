// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.dev.lidar.VelodynePosListener;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32ePosEvent;
import ch.ethz.idsc.retina.util.IntervalClock;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

public class LidarPanoramaFrame implements LidarPanoramaListener, VelodynePosListener {
  public static final int SCALE_Y = 3;
  // ---
  public final JFrame jFrame = new JFrame();
  private LidarPanorama lidarPanorama;
  private Hdl32ePosEvent hdl32ePosEvent;
  private final IntervalClock intervalClock = new IntervalClock();
  JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D graphics = (Graphics2D) g;
      final int height = 32 * SCALE_Y;
      List<String> list = new LinkedList<>();
      {
        LidarPanorama lidarPanoramaRef = lidarPanorama;
        if (Objects.nonNull(lidarPanoramaRef)) {
          list.add("width=" + lidarPanoramaRef.getWidth());
          BufferedImage bufferedImage = lidarPanoramaRef.distances();
          final int width = bufferedImage.getWidth();
          graphics.drawImage(lidarPanoramaRef.distances(), 0, 0, width, height, null);
          graphics.drawImage(lidarPanoramaRef.intensity(), 0, 16 + height, width, height, null);
        }
      }
      {
        Hdl32ePosEvent posRef = hdl32ePosEvent;
        if (Objects.nonNull(posRef)) {
          list.add(posRef.nmea());
          list.add("temp=" + Tensors.vectorDouble(posRef.temp).map(Round._1));
          list.add("gyro=" + Tensors.vectorDouble(posRef.gyro).map(Round._2));
          list.add("accx=" + Tensors.vectorDouble(posRef.accx).map(Round._2));
          list.add("accy=" + Tensors.vectorDouble(posRef.accy).map(Round._2));
          render(graphics, list, 0, 2 * (16 + height));
        }
      }
      graphics.setColor(Color.RED);
      graphics.drawString(String.format("%4.1f Hz", intervalClock.hertz()), 0, 20);
    }
  };

  private static void render(Graphics2D graphics, List<String> list, int x, int y) {
    for (String string : list) {
      graphics.drawString(string, x, y);
      y += 16;
    }
  }

  public LidarPanoramaFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setContentPane(jComponent);
  }

  @Override
  public void lidarPanorama(LidarPanorama lidarPanorama) {
    this.lidarPanorama = lidarPanorama;
    jComponent.repaint();
  }

  @Override
  public void velodynePos(VelodynePosEvent velodynePosEvent) {
    this.hdl32ePosEvent = (Hdl32ePosEvent) velodynePosEvent;
  }

  @Override
  public void close() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }
}
