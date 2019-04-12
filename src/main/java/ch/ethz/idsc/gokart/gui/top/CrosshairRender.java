// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.owl.gui.ColorLookup;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.red.Max;

public class CrosshairRender implements RenderInterface {
  private static final Tensor POLYGON = CirclePoints.of(8).multiply(RealScalar.of(0.3));
  private static final Tensor CIRCLE = CirclePoints.of(28);
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 12);
  // ---
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final ColorDataIndexed colorDataIndexed;
  private final Tensor circles;
  private final Tensor lineX;
  private final Tensor lineY;

  protected CrosshairRender(int limit, ColorDataGradient colorDataGradient, Tensor circles) {
    boundedLinkedList = new BoundedLinkedList<>(limit);
    colorDataIndexed = ColorLookup.decreasing(limit, colorDataGradient).deriveWithAlpha(64);
    this.circles = circles;
    Scalar max = circles.stream().reduce(Max::of).get().Get();
    lineX = Tensors.matrix(new Scalar[][] { { max.negate(), RealScalar.ZERO }, { max, RealScalar.ZERO } });
    lineY = Tensors.matrix(new Scalar[][] { { RealScalar.ZERO, max.negate() }, { RealScalar.ZERO, max } });
  }

  protected final void push_end(Tensor vector) {
    synchronized (boundedLinkedList) {
      boundedLinkedList.add(vector);
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(new Color(128, 128, 128, 128));
    graphics.draw(geometricLayer.toPath2D(lineX));
    graphics.draw(geometricLayer.toPath2D(lineY));
    // ---
    graphics.setFont(FONT);
    for (Tensor _x : circles) {
      Scalar x = _x.Get();
      graphics.setColor(new Color(128, 128, 128, 128));
      geometricLayer.pushMatrix(DiagonalMatrix.of(x, x, RealScalar.ONE));
      graphics.draw(geometricLayer.toPath2D(CIRCLE, true));
      graphics.setColor(new Color(64, 64, 64, 192));
      Point2D point2d = geometricLayer.toPoint2D(0, -1);
      graphics.drawString("" + x, (int) point2d.getX(), (int) point2d.getY());
      geometricLayer.popMatrix();
    }
    // ---
    synchronized (boundedLinkedList) {
      int count = 0;
      for (Tensor velXY : boundedLinkedList) {
        geometricLayer.pushMatrix(Se2Utils.toSE2Translation(velXY));
        graphics.setColor(colorDataIndexed.getColor(count));
        graphics.fill(geometricLayer.toPath2D(POLYGON));
        geometricLayer.popMatrix();
        ++count;
      }
    }
  }
}
