// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvents;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnListener;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.AxisAlignedBox;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

public class AngularSlipRender implements RenderInterface {
  private static final AxisAlignedBox AXIS_ALIGNED_BOX = new AxisAlignedBox(RealScalar.of(0.7));
  /** max range in model space == 12
   * max rate of gokart == pi [rad/s] */
  private static final Tensor DIAGONAL = DiagonalMatrix.of(1, 12 / Math.PI, 1);
  // ---
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  public GokartPoseListener gokartPoseListener = gokartPoseEvent -> this.gokartPoseEvent = gokartPoseEvent;
  // ---
  private SteerColumnEvent steerColumnEvent = SteerColumnEvents.UNKNOWN;
  public SteerColumnListener steerColumnListener = //
      gokartStatusEvent -> this.steerColumnEvent = gokartStatusEvent;
  private final Tensor matrix;

  public AngularSlipRender(Tensor matrix) {
    this.matrix = matrix;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(matrix);
    geometricLayer.pushMatrix(DIAGONAL);
    {
      Tensor tensor = Subdivide.of(-2, 2, 4);
      FontMetrics fontMetrics = graphics.getFontMetrics();
      for (Tensor _y : tensor) {
        Scalar y = _y.Get();
        graphics.setColor(CrosshairRender.COLOR_CIRCLE);
        graphics.draw(geometricLayer.toLine2D(Tensors.of(RealScalar.of(-10.5), y), Tensors.of(RealScalar.of(-12.5), y)));
        graphics.setColor(CrosshairRender.COLOR_FONT);
        Point2D point2d = geometricLayer.toPoint2D(Tensors.of(RealScalar.of(-13.5), y));
        String string = "" + y;
        int halfWidth = fontMetrics.stringWidth(string) / 2;
        graphics.drawString(string, (int) point2d.getX() - halfWidth, (int) point2d.getY());
      }
    }
    if (steerColumnEvent.isSteerColumnCalibrated()) {
      AngularSlip angularSlip = new AngularSlip( //
          gokartPoseEvent.getVelocity(), //
          steerMapping.getRatioFromSCE(steerColumnEvent));
      Scalar gyroZ = angularSlip.gyroZ();
      Scalar wantedRotationRate = angularSlip.wantedRotationRate();
      // ---
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(Tensors.vectorDouble(-11, 0)));
      graphics.setColor(GroundSpeedRender.COLOR_VELOCITY);
      graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongY(Magnitude.PER_SECOND.apply(gyroZ))));
      geometricLayer.popMatrix();
      // ---
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(Tensors.vectorDouble(-12, 0)));
      graphics.setColor(Color.BLUE);
      graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongY(Magnitude.PER_SECOND.apply(wantedRotationRate))));
      geometricLayer.popMatrix();
    }
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
  }
}
