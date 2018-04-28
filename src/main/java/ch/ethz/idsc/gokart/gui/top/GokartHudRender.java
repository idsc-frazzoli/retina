// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Round;

/** head up display for velocity and angular rate */
class GokartHudRender implements RenderInterface, RimoGetListener {
  public static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 25);
  private static final int SEPX = 200;
  // ---
  private RimoGetEvent rimoGetEvent;
  private final JoystickLcmClient joystickLcmClient;

  public GokartHudRender(JoystickLcmClient joystickLcmClient) {
    this.joystickLcmClient = joystickLcmClient;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(rimoGetEvent)) {
      Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
      Scalar rate = ChassisGeometry.GLOBAL.odometryTurningRate(rimoGetEvent);
      graphics.setFont(FONT);
      graphics.setColor(Color.BLUE);
      graphics.drawString("Velocity", 0, 40);
      graphics.drawString("" + speed.map(Round._2), SEPX, 40);
      graphics.drawString("TurningRate", 0, 40 + 30);
      graphics.drawString("" + rate.map(Round._2), SEPX, 40 + 30);
    }
    {
      Optional<JoystickEvent> optional = joystickLcmClient.getJoystick();
      if (optional.isPresent()) {
        GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) optional.get();
        graphics.setFont(FONT);
        graphics.setColor(Color.BLUE);
        graphics.drawString("Autonomous", 0, 40 + 60);
        boolean isAutonomousPressed = gokartJoystickInterface.isAutonomousPressed();
        graphics.setColor(isAutonomousPressed ? Color.RED : Color.BLUE);
        graphics.drawString("" + isAutonomousPressed, SEPX, 40 + 60);
      }
    }
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent getEvent) {
    this.rimoGetEvent = getEvent;
  }
}
