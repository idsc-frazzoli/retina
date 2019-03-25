// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.calib.vmu931.PlanarVmu931Imu;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/* package */ class AccelerationRender extends CrosshairRender implements Vmu931ImuFrameListener {
  private static final Tensor DIAGONAL = DiagonalMatrix.of(.15, .15, 1);
  private static final Scalar FILTER = RealScalar.of(0.02);
  // ---
  private final PlanarVmu931Imu planarVmu931Imu = SensorsConfig.getPlanarVmu931Imu();
  private final GeodesicIIR1Filter geodesicIIR1Filter = new GeodesicIIR1Filter(RnGeodesic.INSTANCE, FILTER);
  private final Tensor xya;

  public AccelerationRender(Tensor xya, int limit) {
    super(limit, ColorDataGradients.BONE, Tensors.vector(2, 4));
    this.xya = xya;
  }

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    Tensor accXY = planarVmu931Imu.accXY(vmu931ImuFrame).map(Magnitude.ACCELERATION);
    Tensor tensor = geodesicIIR1Filter.apply(accXY);
    push_end(tensor);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    graphics.setColor(Color.GRAY);
    geometricLayer.pushMatrix(DIAGONAL);
    super.render(geometricLayer, graphics);
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
  }
}
