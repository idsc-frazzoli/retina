// code by jph
package ch.ethz.idsc.retina.alg.slam;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ch.ethz.idsc.retina.util.gui.BufferedImageCopy;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class SlamComponent implements SlamListener {
  private static final JLabel JLABEL = new JLabel();
  private static final int W_HALF = 512;
  // ---
  private final BufferedImageCopy bufferedImageCopy = new BufferedImageCopy();
  SlamEvent _slamEvent;
  final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImageCopy.get(), 0, 0, JLABEL);
      if (Objects.nonNull(_slamEvent)) {
        Tensor pose = _slamEvent.global_pose;
        {
          graphics.setColor(Color.GRAY);
          graphics.drawRect(W_HALF, W_HALF, 2, 2);
        }
        { // draw pose
          final Point point = toPoint(pose.get(Tensor.ALL, 2));
          {
            graphics.setColor(Color.BLUE);
            graphics.drawRect(point.x - 1, point.y - 1, 3, 3);
          }
          {
            graphics.setColor(Color.RED);
            Point d1 = toPoint(pose.dot(Tensors.vector(50, 0, 1)));
            graphics.drawLine(point.x, point.y, d1.x, d1.y);
          }
          {
            graphics.setColor(Color.GREEN);
            Point d1 = toPoint(pose.dot(Tensors.vector(0, 50, 1)));
            graphics.drawLine(point.x, point.y, d1.x, d1.y);
          }
        }
        { // draw lidar
          final List<Tensor> pose_lidar = _slamEvent.pose_lidar;
          graphics.setColor(Color.MAGENTA);
          for (Tensor points : pose_lidar)
            for (Tensor point : points) {
              Point p1 = toPoint(point);
              graphics.fillRect(p1.x, p1.y, 1, 1);
            }
        }
      }
    }
  };

  private Point toPoint(Tensor vector) {
    return new Point( //
        W_HALF + vector.Get(0).number().intValue(), //
        W_HALF - vector.Get(1).number().intValue());
  }

  @Override
  public void slam(SlamEvent slamEvent) {
    bufferedImageCopy.update(slamEvent.bufferedImage);
    _slamEvent = slamEvent;
    jComponent.repaint();
  }
}
