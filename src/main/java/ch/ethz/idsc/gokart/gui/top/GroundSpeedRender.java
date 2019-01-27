// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.ekf.SimpleVelocityEstimation;
import ch.ethz.idsc.gokart.core.ekf.VelocityEstimation;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class GroundSpeedRender implements RenderInterface {
  private final Tensor origin = Tensors.of(RealScalar.ZERO, RealScalar.ZERO);
  private final Scalar scale = RealScalar.of(10);
  // ---
  private final GeodesicIIR1Filter geodesicIIR1Filter = //
      new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(.02));
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
    Tensor velocityXY = Tensors.of(velocity.Get(0), velocity.Get(1));
    Tensor line = Tensors.of(origin, velocityXY.multiply(scale));
    graphics.draw(geometricLayer.toPath2D(line));
    geometricLayer.popMatrix();
  }
}
