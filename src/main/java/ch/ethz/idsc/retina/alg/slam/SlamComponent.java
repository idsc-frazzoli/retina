// code by jph
package ch.ethz.idsc.retina.alg.slam;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ch.ethz.idsc.retina.util.gui.BufferedImageCopy;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class SlamComponent implements SlamListener {
  private final static JLabel JLABEL = new JLabel();
  // ---
  private final BufferedImageCopy bufferedImageCopy = new BufferedImageCopy();
  private Tensor pose = IdentityMatrix.of(3);
  final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImageCopy.get(), 0, 0, JLABEL);
      {
        graphics.setColor(Color.GRAY);
        graphics.drawRect(512, 512, 2, 2);
      }
      final Point point = toPoint(pose.get(Tensor.ALL, 2));
      { // draw pose
        graphics.setColor(Color.BLUE);
        graphics.drawRect(point.x - 1, point.y - 1, 3, 3);
      }
      {
        Point d1 = toPoint(pose.dot(Tensors.vector(50, 0, 1)));
        graphics.setColor(Color.RED);
        graphics.drawLine(point.x, point.y, d1.x, d1.y);
      }
      {
        Point d1 = toPoint(pose.dot(Tensors.vector(0, 50, 1)));
        graphics.setColor(Color.GREEN);
        graphics.drawLine(point.x, point.y, d1.x, d1.y);
      }
    }
  };

  private Point toPoint(Tensor vector) {
    return new Point( //
        512 + vector.Get(0).number().intValue(), //
        512 - vector.Get(1).number().intValue());
  }

  public void setPose(Tensor pose) {
    this.pose = pose;
  }

  @Override
  public void slam(SlamEvent slamEvent) {
    bufferedImageCopy.update(slamEvent.bufferedImage);
    setPose(slamEvent.global_pose); // TODO make this safe from modification
    jComponent.repaint();
  }
}
