// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.core.mpc.MPCControlUpdateListener;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

public class MPCPredictionRender implements MPCControlUpdateListener, RenderInterface {
  private static final Scalar SCALE = RealScalar.of(0.3);
  // ---
  // TODO JPH initialize as empty
  private ControlAndPredictionSteps _controlAndPredictionSteps;

  @Override // from MPCControlUpdateInterface
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    this._controlAndPredictionSteps = controlAndPredictionSteps;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ControlAndPredictionSteps controlAndPredictionSteps = _controlAndPredictionSteps;
    if (Objects.nonNull(controlAndPredictionSteps)) {
      { // draw positions as path
        Tensor positions = controlAndPredictionSteps.toPositions();
        graphics.setColor(Color.GREEN);
        graphics.draw(geometricLayer.toPath2D(positions));
      }
      { // acceleration visualization
        Tensor accelerations = controlAndPredictionSteps.toAccelerations();
        Tensor poses = controlAndPredictionSteps.toXYA();
        for (int index = 0; index < accelerations.length(); ++index) {
          Tensor pose = GokartPoseHelper.toUnitless(poses.get(index));
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(pose));
          Color color = Sign.isPositiveOrZero(accelerations.Get(index)) //
              ? Color.GREEN
              : Color.RED;
          graphics.setColor(color);
          double acc = Magnitude.ACCELERATION.toDouble(accelerations.Get(index));
          Tensor beg = Tensors.vectorDouble(-acc * 0.8, +acc);
          Tensor mid = Tensors.vectorDouble(0, 0);
          Tensor end = Tensors.vectorDouble(-acc * 0.8, -acc);
          graphics.draw(geometricLayer.toPath2D(Tensors.of(beg, mid, end).multiply(SCALE)));
          geometricLayer.popMatrix();
        }
      }
    }
  }
}
