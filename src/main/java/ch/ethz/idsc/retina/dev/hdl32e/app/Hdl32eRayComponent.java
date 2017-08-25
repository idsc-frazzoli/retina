// code by jph
package ch.ethz.idsc.retina.dev.hdl32e.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Objects;

import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePosEvent;
import ch.ethz.idsc.retina.util.Stopwatch;
import ch.ethz.idsc.tensor.img.Hue;

class Hdl32eRayComponent {
  private final Stopwatch stopwatch = new Stopwatch();
  private int zoom = 0;
  RayContainer rayContainer;
  Hdl32ePosEvent hdl32ePosEvent;
  final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      long period = stopwatch.stop();
      stopwatch.start();
      Graphics2D graphics = (Graphics2D) g;
      Dimension dimension = getSize();
      {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, dimension.width, dimension.height);
        graphics.setColor(Color.GRAY);
      }
      final int midx = dimension.width / 2;
      final int midy = dimension.height / 2;
      {
        RayContainer ref = rayContainer;
        if (Objects.nonNull(ref)) {
          int point = 0;
          float scale = (float) (4 * Math.pow(2.0, zoom));
          for (int c = 0; c < ref.position.length; c += 3) {
            float x = ref.position[c];
            float y = ref.position[c + 1];
            float z = ref.position[c + 2];
            double alpha = (ref.intensity[point] & 0xff) / 255.0;
            Color color = Hue.of(z, 1, 1, alpha);
            graphics.setColor(color);
            graphics.fill(new Rectangle(Math.round(midx + x * scale), Math.round(midy - y * scale), 1, 1));
            ++point;
          }
          graphics.setColor(Color.GRAY);
          graphics.drawString("" + ref.size(), 0, 10);
        }
      }
      {
        Hdl32ePosEvent ref = hdl32ePosEvent;
        if (Objects.nonNull(ref)) {
          graphics.setColor(Color.GRAY);
          graphics.drawString("" + ref.nmea(), 0, 30);
        }
      }
      graphics.setColor(Color.RED);
      graphics.drawString(String.format("%4.1f Hz", (1.0e9 / period)), 0, 20);
    }
  };

  public Hdl32eRayComponent() {
    jComponent.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        zoom -= mouseWheelEvent.getWheelRotation();
        // urg04lxRender.setZoom(zoom);
        System.out.println(zoom);
        jComponent.repaint();
      }
    });
  }
}
