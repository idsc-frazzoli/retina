// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owly.gui.GeometricLayer;
import ch.ethz.idsc.owly.gui.RenderInterface;
import ch.ethz.idsc.retina.dev.steer.TurningGeometry;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusListener;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** renders point of rotation as small dot in plane */
public class TrigonometryRender implements RenderInterface {
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteeringCalibrated()) {
      Scalar XAD = ChassisGeometry.GLOBAL.xAxleDistanceMeter(); // axle distance
      Optional<Scalar> optional = TurningGeometry.offset_y(XAD, gokartStatusEvent.getSteeringAngle());
      if (optional.isPresent()) {
        Scalar offset_y = optional.get();
        Scalar XAR = ChassisGeometry.GLOBAL.xAxleRearMeter();
        Tensor center = Tensors.of(XAR, offset_y);
        Point2D point2D = geometricLayer.toPoint2D(center);
        graphics.setColor(Color.PINK);
        graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
      }
    }
  }
}
