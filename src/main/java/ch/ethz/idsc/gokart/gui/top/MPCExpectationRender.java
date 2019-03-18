// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.mpc.MPCInformationProvider;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCExpectationRender implements RenderInterface, RimoGetListener {
  private static final Tensor DIAGONAL = DiagonalMatrix.of(.2, .1, 1);
  private static final Tensor MPCLINE = Tensors.fromString("{{0,0},{0,3}}");
  private static final Tensor RIMOLINE = Tensors.fromString("{{0,-3},{0,0}}");
  private final Tensor xya;
  private Scalar currentRimoAcc = Quantity.of(-3, SI.ONE);
  private Scalar currentMPCAcc = Quantity.of(-4, SI.ONE);
  private final IntervalClock rimoClock = new IntervalClock();
  private Scalar lastTangentSpeed = Quantity.of(0, SI.VELOCITY);
  /** acceleration filter */
  private final GeodesicIIR1Filter geodesicIIR1Filter = //
      new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(.1));

  public MPCExpectationRender(Tensor xya) {
    this.xya = xya;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    currentMPCAcc = MPCInformationProvider.getInstance().getFirstWantedAcceleration();
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    geometricLayer.pushMatrix(DIAGONAL);
    // rimo line
    Tensor rimoAccXY = Tensors.of(Quantity.of(currentRimoAcc.number(), SI.ONE), Quantity.of(0, SI.ONE));
    geometricLayer.pushMatrix(Se2Utils.toSE2Translation(rimoAccXY));
    graphics.setColor(Color.BLUE);
    graphics.draw(geometricLayer.toPath2D(RIMOLINE));
    geometricLayer.popMatrix();
    // mpc line
    Tensor mpcAccXY = Tensors.of(Quantity.of(currentMPCAcc.number(), SI.ONE), Quantity.of(0, SI.ONE));
    geometricLayer.pushMatrix(Se2Utils.toSE2Translation(mpcAccXY));
    graphics.setColor(Color.RED);
    graphics.draw(geometricLayer.toPath2D(MPCLINE));
    geometricLayer.popMatrix();
    //
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
  }

  @Override
  public void getEvent(RimoGetEvent getEvent) {
    Scalar currentTangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
    Scalar acceleration = currentTangentSpeed//
        .subtract(lastTangentSpeed)//
        .divide(Quantity.of(rimoClock.seconds(), SI.SECOND));
    lastTangentSpeed = currentTangentSpeed;
    currentRimoAcc = (Scalar) geodesicIIR1Filter.apply(acceleration);
  }
}
