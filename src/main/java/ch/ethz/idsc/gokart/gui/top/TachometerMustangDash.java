// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.gokart.calib.steer.RimoTwdOdometry;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.owl.gui.ColorLookup;
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
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class TachometerMustangDash implements RenderInterface, RimoGetListener {
  private static final ScalarUnaryOperator SCALAR_UNARY_OPERATOR = //
      QuantityMagnitude.SI().in(NonSI.KM_PER_HOUR);
  private static final int STEPS = 50;
  private static final Clip CLIP = Clips.positive(STEPS);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = //
      ColorLookup.increasing(STEPS + 1, ColorDataGradients.BONE).deriveWithAlpha(192);
  private static final Tensor ANGLES = Subdivide.of(2, -2, STEPS).map(AngleVector::of);
  // ---
  private final Tensor matrix;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();

  public TachometerMustangDash(Tensor matrix) {
    this.matrix = matrix;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    geometricLayer.pushMatrix(matrix);
    Scalar speed = SCALAR_UNARY_OPERATOR.apply(RimoTwdOdometry.tangentSpeed(rimoGetEvent));
    speed = CLIP.apply(speed);
    final int steps = speed.number().intValue();
    graphics.setStroke(new BasicStroke(geometricLayer.model2pixelWidth(0.5)));
    for (int count = 0; count < steps; ++count) {
      graphics.setColor(COLOR_DATA_INDEXED.getColor(steps - count));
      Tensor vector = ANGLES.get(count);
      Path2D path2d = geometricLayer.toPath2D(Tensors.of( //
          vector.multiply(RealScalar.of(10.8)), //
          vector.multiply(RealScalar.of(11.7))));
      graphics.draw(path2d);
    }
    graphics.setColor(Color.DARK_GRAY);
    graphics.setStroke(new BasicStroke(geometricLayer.model2pixelWidth(0.2)));
    for (Tensor _a : Subdivide.of(-2, 2, STEPS / 5)) {
      Scalar angle = _a.Get();
      Tensor vector = AngleVector.of(angle);
      Path2D path2d = geometricLayer.toPath2D(Tensors.of( //
          vector.multiply(RealScalar.of(10.5)), //
          vector.multiply(RealScalar.of(11.8))));
      graphics.draw(path2d);
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
