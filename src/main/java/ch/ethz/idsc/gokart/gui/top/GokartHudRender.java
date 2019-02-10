// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvents;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Round;

/** head up display for velocity and angular rate */
class GokartHudRender implements RenderInterface {
  private static final Font FONT_SMALL = new Font(Font.DIALOG, Font.PLAIN, 12);
  private static final Font FONT_LARGE = new Font(Font.DIALOG, Font.BOLD, 25);
  private static final int SEPX = 200;
  // ---
  private final GokartPoseInterface gokartPoseInterface;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.create(0, 0);
  private SteerGetEvent steerGetEvent = SteerGetEvents.ZEROS;
  final RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent getEvent) {
      rimoGetEvent = getEvent;
    }
  };
  final SteerGetListener steerGetListener = new SteerGetListener() {
    @Override
    public void getEvent(SteerGetEvent getEvent) {
      System.out.println("update");
      steerGetEvent = getEvent;
    }
  };

  public GokartHudRender(GokartPoseInterface gokartPoseInterface) {
    this.gokartPoseInterface = gokartPoseInterface;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    {
      Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
      Scalar rate = ChassisGeometry.GLOBAL.odometryTurningRate(rimoGetEvent);
      graphics.setFont(FONT_LARGE);
      graphics.setColor(Color.BLUE);
      graphics.drawString("Velocity", 0, 40);
      graphics.drawString(speed.map(Round._2).toString(), SEPX, 40);
      graphics.drawString("TurningRate", 0, 40 + 30);
      graphics.drawString(rate.map(Round._2).toString(), SEPX, 40 + 30);
    }
    {
      graphics.setFont(FONT_SMALL);
      graphics.setColor(Color.GRAY);
      graphics.drawString(gokartPoseInterface.getPose().map(Round._2).toString(), SEPX, 12);
    }
    {
      graphics.setFont(FONT_LARGE);
      graphics.setColor(Color.BLUE);
      graphics.drawString("Autonomous", 0, 40 + 60);
      boolean isAutonomousPressed = steerGetEvent.isActive();
      graphics.setColor(isAutonomousPressed ? Color.RED : Color.BLUE);
      graphics.drawString("" + isAutonomousPressed, SEPX, 40 + 60);
    }
  }
}
