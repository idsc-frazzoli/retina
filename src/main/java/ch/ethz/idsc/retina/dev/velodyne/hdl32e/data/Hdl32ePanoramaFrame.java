// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.velodyne.VelodynePosEvent;
import ch.ethz.idsc.retina.dev.velodyne.VelodynePosEventListener;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32ePosEvent;
import ch.ethz.idsc.retina.util.Stopwatch;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

public class Hdl32ePanoramaFrame implements Hdl32ePanoramaListener, VelodynePosEventListener {
  public static final int SCALE_Y = 3;
  // ---
  public final JFrame jFrame = new JFrame();
  private Hdl32ePanorama hdl32ePanorama;
  private Hdl32ePosEvent hdl32ePosEvent;
  private final Stopwatch stopwatch = new Stopwatch();
  JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      long period = stopwatch.stop();
      stopwatch.start();
      Graphics2D graphics = (Graphics2D) g;
      final int height = 32 * SCALE_Y;
      List<String> list = new LinkedList<>();
      {
        Hdl32ePanorama hdl32ePanoramaRef = hdl32ePanorama;
        if (Objects.nonNull(hdl32ePanoramaRef)) {
          list.add("width=" + hdl32ePanoramaRef.getWidth());
          graphics.drawImage(hdl32ePanoramaRef.distances(), 0, 0, Hdl32ePanorama.MAX_WIDTH, height, jFrame);
          graphics.drawImage(hdl32ePanoramaRef.intensity(), 0, 16 + height, Hdl32ePanorama.MAX_WIDTH, height, jFrame);
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
      graphics.drawString(String.format("%4.1f Hz", (1.0e9 / period)), 0, 20);
    }
  };

  private static void render(Graphics2D graphics, List<String> list, int x, int y) {
    for (String string : list) {
      graphics.drawString(string, x, y);
      y += 16;
    }
  }

  public Hdl32ePanoramaFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 1700, 300);
    jFrame.setContentPane(jComponent);
    jFrame.setVisible(true);
  }

  @Override
  public void panorama(Hdl32ePanorama hdl32ePanorama) {
    this.hdl32ePanorama = hdl32ePanorama;
    jComponent.repaint();
  }

  @Override
  public void positioning(VelodynePosEvent velodynePosEvent) {
    this.hdl32ePosEvent = (Hdl32ePosEvent) velodynePosEvent;
  }

  @Override
  public void close() {
    jFrame.dispose();
  }
}
