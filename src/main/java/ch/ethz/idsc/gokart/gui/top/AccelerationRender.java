// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.owl.gui.ColorLookup;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/* package */ class AccelerationRender implements RenderInterface {
  private static final Tensor POLYGON = Tensor.of(CirclePoints.of(8).stream().map(row -> row.pmul(Tensors.vector(.2, .4))));
  private static final Tensor LINE_X = Tensors.fromString("{{-1,0},{0.5,0}}");
  private static final Tensor LINE_Y = Tensors.fromString("{{0,-1},{0,1}}");
  private static final Tensor DIAGONAL = DiagonalMatrix.of(.2, .1, 1);
  // ---
  private final GeodesicIIR1Filter geodesicIIR1Filter = //
      new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(.02));
  private final Tensor xya;
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final ColorDataIndexed colorDataIndexed;

  public AccelerationRender(Tensor xya, int limit) {
    this.xya = xya;
    boundedLinkedList = new BoundedLinkedList<>(limit);
    colorDataIndexed = ColorLookup.decreasing(limit, ColorDataGradients.BONE).deriveWithAlpha(128);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    graphics.setColor(Color.GRAY);
    graphics.draw(geometricLayer.toPath2D(LINE_X));
    graphics.draw(geometricLayer.toPath2D(LINE_Y));
    geometricLayer.pushMatrix(DIAGONAL);
    int count = 0;
    synchronized (boundedLinkedList) {
      for (Tensor accXY : boundedLinkedList) {
        geometricLayer.pushMatrix(Se2Utils.toSE2Translation(accXY));
        graphics.setColor(colorDataIndexed.getColor(count));
        graphics.fill(geometricLayer.toPath2D(POLYGON));
        geometricLayer.popMatrix();
        ++count;
      }
    }
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
  }

  /** @param accXY vector of length 2 with units [m*s^-2] */
  public void setAccelerationXY(Tensor accXY) {
    synchronized (boundedLinkedList) {
      boundedLinkedList.add(geodesicIIR1Filter.apply(accXY.map(Magnitude.ACCELERATION)));
    }
  }
}
