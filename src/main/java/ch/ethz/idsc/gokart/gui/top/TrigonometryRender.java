// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.GokartStatusEvents;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.owl.car.math.TurningGeometry;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** renders point of rotation as small dot in plane */
public class TrigonometryRender implements RenderInterface {
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  public final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;
  // ---
  private GokartStatusEvent gokartStatusEvent = GokartStatusEvents.UNKNOWN;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;
  // ---
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (gokartStatusEvent.isSteerColumnCalibrated()) {
      geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
      Scalar ratio = steerMapping.getRatioFromSCE(gokartStatusEvent); // <- calibration checked
      Optional<Scalar> optional = TurningGeometry.offset_y(ratio);
      if (optional.isPresent()) { // draw point of rotation when assuming no slip
        Scalar offset_y = Magnitude.METER.apply(optional.get());
        Tensor center = Tensors.of(RealScalar.ZERO, offset_y);
        Point2D point2D = geometricLayer.toPoint2D(center);
        graphics.setColor(Color.PINK);
        graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
      }
      geometricLayer.popMatrix();
    }
  }
}
