// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class VelocityIndicatorRender implements RenderInterface, RimoGetListener {
  private final Tensor xya;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.create(0, 0);

  public VelocityIndicatorRender(Tensor xya) {
    this.xya = xya;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in(Unit.of("km*h^-1"));
    Scalar speed = scalarUnaryOperator.apply(ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent));
    Clip clip = Clips.interval(0, 60);
    speed = clip.apply(speed);
    Font font = graphics.getFont();
    {
      Point2D point2d = geometricLayer.toPoint2D(2, 0);
      float model2pixelWidth = geometricLayer.model2pixelWidth(0.3);
      graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) model2pixelWidth));
      FontMetrics fontMetrics = graphics.getFontMetrics();
      String string = speed.map(Round.FUNCTION).toString();
      int stringWidth = fontMetrics.stringWidth(string);
      graphics.setColor(Color.DARK_GRAY);
      graphics.drawString(string, (float) point2d.getX() - stringWidth / 2, (float) point2d.getY());
    }
    graphics.setFont(font);
    geometricLayer.popMatrix();
  }

  @Override
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }
}
