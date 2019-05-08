// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.gokart.calib.steer.GokartStatusEvents;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.owl.car.math.BicycleAngularSlip;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.sca.N;

public class SteerTurnRender implements RenderInterface {
  private static final Tensor ORIGIN = Array.zeros(2).map(N.DOUBLE);
  // ---
  private GokartStatusEvent gokartStatusEvent = GokartStatusEvents.UNKNOWN;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  public final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final BicycleAngularSlip bicycleAngularSlip = ChassisGeometry.GLOBAL.getBicycleAngularSlip();
  private final Tensor matrix;

  public SteerTurnRender(Tensor matrix) {
    this.matrix = matrix;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(matrix);
    if (gokartStatusEvent.isSteerColumnCalibrated() && gokartPoseEvent.hasVelocity()) {
      Scalar theta = steerMapping.getRatioFromSCE(gokartStatusEvent.getSteerColumnEncoderCentered());
      graphics.setColor(Color.MAGENTA);
      graphics.draw(geometricLayer.toPath2D(Tensors.of(ORIGIN, AngleVector.of(theta))));
      Scalar rotationRate = bicycleAngularSlip.wantedRotationRate(theta, gokartPoseEvent.getVelocity().Get(0));
      graphics.setColor(Color.BLUE);
      graphics.draw(geometricLayer.toPath2D(Tensors.of(ORIGIN, //
          AngleVector.of(Magnitude.PER_SECOND.apply(rotationRate)))));
      // ---
      graphics.setColor(Color.GREEN);
      graphics.draw(geometricLayer.toPath2D(Tensors.of(ORIGIN, //
          AngleVector.of(Magnitude.PER_SECOND.apply(gokartPoseEvent.getGyroZ())))));
    } else {
      graphics.setColor(Color.RED);
      Point2D point2d = geometricLayer.toPoint2D(0, 0);
      graphics.drawString("no vel", (int) point2d.getX(), (int) point2d.getY());
    }
    geometricLayer.popMatrix();
  }
}
