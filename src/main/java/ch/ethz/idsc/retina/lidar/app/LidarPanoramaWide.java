// code by jph
package ch.ethz.idsc.retina.lidar.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JComponent;

import ch.ethz.idsc.retina.util.time.IntervalClock;

public class LidarPanoramaWide {
  public static final int SCALE_Y = 3;
  // ---
  public LidarPanorama _lidarPanorama;
  private final IntervalClock intervalClock = new IntervalClock();
  final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D graphics = (Graphics2D) g;
      final int height = 32 * SCALE_Y;
      {
        LidarPanorama lidarPanorama = _lidarPanorama;
        if (Objects.nonNull(lidarPanorama)) {
          BufferedImage bufferedImage = lidarPanorama.distances();
          final int width = bufferedImage.getWidth();
          graphics.drawImage(lidarPanorama.distances(), 0, 0, width, height, null);
          graphics.drawImage(lidarPanorama.intensity(), 0, 16 + height, width, height, null);
        }
      }
      graphics.setColor(Color.RED);
      graphics.drawString(String.format("%4.1f Hz", intervalClock.hertz()), 0, 20);
    }
  };
}
