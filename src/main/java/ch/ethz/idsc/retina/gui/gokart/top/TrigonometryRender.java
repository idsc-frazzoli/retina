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
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class TrigonometryRender implements RenderInterface {
  // TODO redundant
  public static final Scalar LR = DoubleScalar.of(-0.47); // rear axle distance from COG [m]
  /** axle distance */
  // TODO redundant
  private static final Scalar AXD = RealScalar.of(1.2);
  // ---
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteeringCalibrated()) {
      Optional<Scalar> optional = TurningGeometry.offset_y(AXD, gokartStatusEvent.getSteeringAngle());
      if (optional.isPresent()) {
        Scalar offset_y = optional.get();
        Tensor center = Tensors.of(LR, offset_y);
        Point2D point2D = geometricLayer.toPoint2D(center);
        graphics.setColor(Color.PINK);
        graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
      }
    }
  }
}
