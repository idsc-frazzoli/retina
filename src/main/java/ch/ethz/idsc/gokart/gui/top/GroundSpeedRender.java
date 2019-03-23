// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.PoseVelocityInterface;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

/* package */ class GroundSpeedRender implements RenderInterface {
  private static final Stroke STROKE_DEFAULT = new BasicStroke();
  private static final Tensor ORIGIN = Array.zeros(2);
  private static final Scalar SCALE = RealScalar.of(0.1);
  // ---
  private final PoseVelocityInterface positionVelocityEstimation;
  private final Tensor xya;

  public GroundSpeedRender(PoseVelocityInterface positionVelocityEstimation, Tensor xya) {
    this.positionVelocityEstimation = positionVelocityEstimation;
    this.xya = xya;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.isNull(positionVelocityEstimation))
      return;
    Tensor line = Tensors.of(ORIGIN, positionVelocityEstimation.getVelocityXY().multiply(SCALE));
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    graphics.setColor(Color.BLUE);
    graphics.setStroke(new BasicStroke(geometricLayer.model2pixelWidth(0.03)));
    GraphicsUtil.setQualityHigh(graphics);
    graphics.draw(geometricLayer.toPath2D(line));
    GraphicsUtil.setQualityDefault(graphics);
    graphics.setStroke(STROKE_DEFAULT);
    geometricLayer.popMatrix();
  }
}
