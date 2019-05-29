// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.calib.steer.RimoTwdOdometry;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
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
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  private SteerGetEvent steerGetEvent = SteerGetEvents.ZEROS;
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  final SteerGetListener steerGetListener = getEvent -> steerGetEvent = getEvent;
  final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    {
      Scalar speed = RimoTwdOdometry.tangentSpeed(rimoGetEvent);
      Scalar rate = RimoTwdOdometry.turningRate(rimoGetEvent);
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
      // TODO also show vel
      graphics.drawString(gokartPoseEvent.getPose().map(Round._2).toString(), SEPX, 12);
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
