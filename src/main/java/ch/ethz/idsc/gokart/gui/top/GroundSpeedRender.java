// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.ekf.SimpleVelocityEstimation;
import ch.ethz.idsc.gokart.core.ekf.VelocityEstimation;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

/* package */ class GroundSpeedRender implements RenderInterface {
  private static final Tensor ORIGIN = Array.zeros(2);
  private static final Scalar SCALE = RealScalar.of(0.1);
  // ---
  private final Tensor xya;

  public GroundSpeedRender(Tensor xya) {
    this.xya = xya;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    VelocityEstimation velocityEstimation = ModuleAuto.INSTANCE.getInstance(SimpleVelocityEstimation.class);
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    graphics.setColor(Color.BLUE);
    Tensor velocity = velocityEstimation.getVelocity();
    // TODO MH why rebuild the tensor and not extract?
    Tensor velocityXY = Tensors.of(velocity.Get(0), velocity.Get(1));
    Tensor line = Tensors.of(ORIGIN, velocityXY.multiply(SCALE));
    graphics.draw(geometricLayer.toPath2D(line));
    geometricLayer.popMatrix();
  }
}
