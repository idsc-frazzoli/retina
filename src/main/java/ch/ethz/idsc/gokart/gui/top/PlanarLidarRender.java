// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** draw polygon */
class PlanarLidarRender extends LidarRender {
  private static final Tensor ORIGIN = Tensors.vectorDouble(0, 0).unmodifiable();

  public PlanarLidarRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(supplier.get()));
    Tensor points = _points.copy();
    points.append(ORIGIN);
    graphics.setColor(color);
    graphics.fill(geometricLayer.toPath2D(points));
    geometricLayer.popMatrix();
  }
}
