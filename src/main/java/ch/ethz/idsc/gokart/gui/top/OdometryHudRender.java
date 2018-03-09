// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Round;

/** head up display for velocity and angular rate */
class OdometryHudRender implements RenderInterface, RimoGetListener {
  public static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 25);
  // ---
  private RimoGetEvent rimoGetEvent;

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(rimoGetEvent)) {
      Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
      Scalar rate = ChassisGeometry.GLOBAL.odometryTurningRate(rimoGetEvent);
      graphics.setFont(FONT);
      graphics.setColor(Color.BLUE);
      graphics.drawString("Velocity", 0, 40);
      graphics.drawString("" + speed.map(Round._2), 200, 40);
      graphics.drawString("TurningRate", 0, 40 + 30);
      graphics.drawString("" + rate.map(Round._2), 200, 40 + 30);
    }
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent getEvent) {
    this.rimoGetEvent = getEvent;
  }
}
