// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class TachometerMustangDash implements RenderInterface, RimoGetListener {
  private static final ScalarUnaryOperator SCALAR_UNARY_OPERATOR = //
      QuantityMagnitude.SI().in(NonSI.KM_PER_HOUR);
  // ---
  private final Tensor matrix;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();

  public TachometerMustangDash(Tensor matrix) {
    this.matrix = matrix;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    geometricLayer.pushMatrix(matrix);
    graphics.setColor(Color.DARK_GRAY);
    graphics.setStroke(new BasicStroke(geometricLayer.model2pixelWidth(0.2)));
    for (Tensor _a : Subdivide.of(-2, 2, 12)) {
      Scalar angle = _a.Get();
      Tensor vector = AngleVector.of(angle);
      Path2D path2d = geometricLayer.toPath2D(Tensors.of( //
          vector.multiply(RealScalar.of(10.5)), //
          vector.multiply(RealScalar.of(11.8))));
      graphics.draw(path2d);
    }
    Scalar speed = SCALAR_UNARY_OPERATOR.apply(ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent));
    Clip clip = Clips.interval(0, 60);
    speed = clip.apply(speed);
    final int steps = speed.number().intValue();
    int count = 0;
    graphics.setStroke(new BasicStroke(geometricLayer.model2pixelWidth(0.3)));
    for (Tensor _a : Subdivide.of(2, -2, 12 * 6)) {
      Scalar angle = _a.Get();
      Tensor rgba = ColorDataGradients.BONE.apply(Clips.unit().apply(RealScalar.of(0.3 + (steps - count) * 0.01)));
      Color color = ColorFormat.toColor(rgba);
      // color = new Color(color.getGreen(),color.getBlue(),color.getRed(),128+64);
      graphics.setColor(color);
      Tensor vector = AngleVector.of(angle);
      Path2D path2d = geometricLayer.toPath2D(Tensors.of( //
          vector.multiply(RealScalar.of(10.8)), //
          vector.multiply(RealScalar.of(11.7) //
          )));
      graphics.draw(path2d);
      count++;
      if (steps < count)
        break;
    }
    {
      Point2D point2d = geometricLayer.toPoint2D(0, 0);
      float model2pixelWidth = geometricLayer.model2pixelWidth(0.3);
      graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) model2pixelWidth));
      FontMetrics fontMetrics = graphics.getFontMetrics();
      String string = speed.map(Round.FUNCTION).toString();
      int stringWidth = fontMetrics.stringWidth(string);
      graphics.drawString(string, (float) point2d.getX() - stringWidth / 2, (float) point2d.getY());
    }
    geometricLayer.popMatrix();
    GraphicsUtil.setQualityDefault(graphics);
    graphics.setStroke(new BasicStroke());
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }
}
