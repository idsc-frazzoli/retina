// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.mpc.MPCInformationProvider;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum MPCPredictionRender implements RenderInterface {
  INSTANCE;
  // ---
  private static final MPCInformationProvider MPC_INFORMATION_PROVIDER = MPCInformationProvider.getInstance();
  // TODO JPH/MH the units of scale are ignored -> remove unit of scale
  private static final Scalar SCALE = Quantity.of(0.3, SI.METER);

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor positions = MPC_INFORMATION_PROVIDER.getPositions();
    if (!Tensors.isEmpty(positions)) {
      graphics.setColor(Color.GREEN);
      graphics.draw(geometricLayer.toPath2D(positions)); // draw positions as path
      // acceleration visualization
      Tensor accelerations = MPC_INFORMATION_PROVIDER.getAccelerations();
      Tensor poses = MPC_INFORMATION_PROVIDER.getXYA();
      for (int i = 0; i < accelerations.length(); ++i) {
        geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(poses.get(i)));
        Color color = Scalars.lessThan(accelerations.Get(i), Quantity.of(0, SI.ACCELERATION)) //
            ? Color.RED
            : Color.GREEN;
        graphics.setColor(color);
        Scalar acc = accelerations.Get(i);
        // TODO JPH/MH use Magnitude.ACC.toDouble...
        Tensor start = Tensors.vector(-acc.number().doubleValue() * 0.8, acc.number().doubleValue());
        Tensor mid = Tensors.vector(0, 0);
        Tensor end = Tensors.vector(-acc.number().doubleValue() * 0.8, -acc.number().doubleValue());
        start = start.multiply(SCALE);
        mid = mid.multiply(SCALE);
        end = end.multiply(SCALE);
        graphics.draw(geometricLayer.toPath2D(Tensors.of(start, mid, end)));
        geometricLayer.popMatrix();
      }
    }
  }
}
