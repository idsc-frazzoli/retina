// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Graphics2D;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.gui.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class PlanarLidarRender extends LidarRender {
  private static final Tensor ORIGIN = Tensors.vectorDouble(0, 0);

  public PlanarLidarRender(Supplier<Tensor> supplier) {
    super(supplier);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor matrix = Se2Utils.toSE2Matrix(supplier.get());
    geometricLayer.pushMatrix(matrix);
    Tensor points = _points.copy();
    points.append(ORIGIN);
    graphics.setColor(color);
    graphics.fill(geometricLayer.toPath2D(points));
    geometricLayer.popMatrix();
  }
}
