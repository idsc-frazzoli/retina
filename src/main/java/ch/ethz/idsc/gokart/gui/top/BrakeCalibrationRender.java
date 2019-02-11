package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.calib.brake.BrakingFunctions;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

public class BrakeCalibrationRender implements RenderInterface {
  private static final Tensor DIAGONAL = DiagonalMatrix.of(.5, .5, 1);
  private final Tensor xya;

  public BrakeCalibrationRender(Tensor xya) {
    this.xya = xya;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    geometricLayer.pushMatrix(DIAGONAL);
    // calibration line
    Scalar calibrationValue = BrakingFunctions.CALIBRATING.getBrakeFadeFactor();
    graphics.setColor(Color.BLUE);
    Tensor polygon = Tensors.of(Tensors.vector(0, 0), Tensors.of(calibrationValue, RealScalar.ZERO));
    graphics.draw(geometricLayer.toPath2D(polygon));
    //
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
  }
}
