// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.gokart.core.mpc.MPCInformationProvider;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCPredictionRender implements RenderInterface {
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor positions = MPCInformationProvider.getInstance().getPositions();
    if (!Tensors.isEmpty(positions)) {
      // draw
      graphics.setColor(Color.GREEN);
      Path2D path2d = geometricLayer.toPath2D(positions);
      graphics.draw(path2d);
      // acceleration visualization
      Tensor accelerations = MPCInformationProvider.getInstance().getAccelerations();
      Tensor poses = MPCInformationProvider.getInstance().getXYA();
      for (int i = 0; i < accelerations.length(); i++) {
        geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(poses.get(i)));
        if (Scalars.lessThan(accelerations.Get(i), Quantity.of(0, SI.ACCELERATION))) {
          graphics.setColor(Color.RED);
        } else {
          graphics.setColor(Color.GREEN);
        }
        Scalar acc = accelerations.Get(i);
        Tensor start = Tensors.vector(-acc.number().doubleValue()*0.8, acc.number().doubleValue());
        Tensor mid = Tensors.vector(0, 0);
        Tensor end = Tensors.vector(-acc.number().doubleValue() * 0.8, -acc.number().doubleValue());
        Scalar scale = Quantity.of(0.3, SI.METER);
        start = start.multiply(scale);
        mid = mid.multiply(scale);
        end = end.multiply(scale);
        graphics.draw(geometricLayer.toPath2D(Tensors.of(start, mid, end)));
        geometricLayer.popMatrix();
      }
    }
  }
}
