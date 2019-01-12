// code by jph
package ch.ethz.idsc.retina.lidar.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JComponent;

import ch.ethz.idsc.retina.util.time.IntervalClock;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.sca.Ceiling;

public class LidarPanoramaTile {
  public static final int SCALE_Y = 3;
  // ---
  final int split = 910;
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
          int tiles = Ceiling.FUNCTION.apply(RationalScalar.of(width, split)).number().intValue();
          for (int tile = 0; tile < tiles; ++tile) {
            int x = tile * split;
            int www = Math.min((tile + 1) * split, width);
            graphics.drawImage(bufferedImage.getSubimage(x, 0, www - x, 16), 0, tile * (height + 16), www - x, height, null);
          }
        }
      }
      graphics.setColor(Color.RED);
      graphics.drawString(String.format("%4.1f Hz", intervalClock.hertz()), 0, 20);
    }
  };
}
