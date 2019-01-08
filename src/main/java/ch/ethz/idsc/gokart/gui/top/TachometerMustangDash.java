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
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class TachometerMustangDash implements RenderInterface, RimoGetListener {
  private final Tensor xya;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.create(0, 0);

  public TachometerMustangDash(Tensor xya) {
    this.xya = xya;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    graphics.setColor(Color.DARK_GRAY);
    graphics.setStroke(new BasicStroke(1.5f));
    for (Tensor _a : Subdivide.of(-2, 2, 12)) {
      Scalar angle = _a.Get();
      Path2D path2d = geometricLayer.toPath2D(Tensors.of(AngleVector.of(angle), AngleVector.of(angle).multiply(RealScalar.of(1.1))));
      graphics.draw(path2d);
    }
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in(Unit.of("km*h^-1"));
    Scalar speed = scalarUnaryOperator.apply(ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent));
    Clip clip = Clip.function(0, 60);
    speed = clip.apply(speed);
    final int steps = speed.multiply(RealScalar.of(1)).number().intValue();
    int count = 0;
    graphics.setStroke(new BasicStroke(2.5f));
    for (Tensor _a : Subdivide.of(2, -2, 12 * 6)) {
      Scalar angle = _a.Get();
      Tensor rgba = ColorDataGradients.BONE.apply(Clip.unit().apply(RealScalar.of(0.3 + (steps - count) * 0.01)));
      Color color = ColorFormat.toColor(rgba);
      // color = new Color(color.getGreen(),color.getBlue(),color.getRed(),128+64);
      graphics.setColor(color);
      Path2D path2d = geometricLayer.toPath2D(Tensors.of( //
          AngleVector.of(angle).multiply(RealScalar.of(0.7)), //
          AngleVector.of(angle).multiply(RealScalar.of(0.95))));
      graphics.draw(path2d);
      count++;
      if (steps < count)
        break;
    }
    {
      Point2D point2d = geometricLayer.toPoint2D(0, 0);
      graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
      FontMetrics fontMetrics = graphics.getFontMetrics();
      String string = speed.map(Round.FUNCTION).toString();
      int stringWidth = fontMetrics.stringWidth(string);
      graphics.drawString(string, (float) point2d.getX() - stringWidth / 2, (float) point2d.getY());
    }
    geometricLayer.popMatrix();
    GraphicsUtil.setQualityDefault(graphics);
  }

  @Override
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }
}
