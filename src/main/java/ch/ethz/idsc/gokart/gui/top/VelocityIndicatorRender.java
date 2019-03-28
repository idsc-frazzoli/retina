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
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** show wheel odometry tangent speed in km/h */
/* package */ class VelocityIndicatorRender implements RenderInterface, RimoGetListener {
  private static final ScalarUnaryOperator KM_PER_HOUR = QuantityMagnitude.SI().in(NonSI.KM_PER_HOUR);
  // ---
  private final Tensor xya;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();

  public VelocityIndicatorRender(Tensor xya) {
    this.xya = xya;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    Font font = graphics.getFont();
    Point2D point2d = geometricLayer.toPoint2D(2, 0);
    float model2pixelWidth = geometricLayer.model2pixelWidth(0.3);
    graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) model2pixelWidth));
    FontMetrics fontMetrics = graphics.getFontMetrics();
    Scalar speed = KM_PER_HOUR.apply(ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent));
    String string = speed.map(Round.FUNCTION).toString();
    int stringWidth = fontMetrics.stringWidth(string);
    graphics.setColor(Color.DARK_GRAY);
    graphics.drawString(string, (float) point2d.getX() - stringWidth / 2, (float) point2d.getY());
    graphics.setFont(font);
    geometricLayer.popMatrix();
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }
}
