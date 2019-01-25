// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.data.BoundedLinkedList;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

public class AccelerationRender implements RenderInterface {
  private static final Tensor POLYGON = Tensor.of(CirclePoints.of(8).stream().map(row -> row.pmul(Tensors.vector(.2, .4))));
  private static final Tensor LINE_X = Tensors.fromString("{{-1,0},{0.5,0}}");
  private static final Tensor LINE_Y = Tensors.fromString("{{0,-1},{0,1}}");
  // ---
  private final GeodesicIIR1Filter geodesicIIR1Filter = //
      new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(.02));
  private final Tensor xya;
  private final int limit;
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  public AccelerationRender(Tensor xya, int limit) {
    this.xya = xya;
    this.limit = limit;
    boundedLinkedList = new BoundedLinkedList<>(limit);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    int count = limit;
    graphics.setColor(Color.GRAY);
    graphics.draw(geometricLayer.toPath2D(LINE_X));
    graphics.draw(geometricLayer.toPath2D(LINE_Y));
    geometricLayer.pushMatrix(DiagonalMatrix.of(.2, .1, 1));
    synchronized (boundedLinkedList) {
      for (Tensor accXY : boundedLinkedList) {
        --count;
        geometricLayer.pushMatrix(Se2Utils.toSE2Translation(accXY));
        Path2D path2d = geometricLayer.toPath2D(POLYGON);
        // TODO pre-compute color table (in owl: ColorLookup)
        int rgb = (int) (count * 255 / (float) limit);
        graphics.setColor(new Color(rgb, rgb, rgb, 128));
        graphics.fill(path2d);
        geometricLayer.popMatrix();
      }
    }
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
  }

  public void setAccelerationXY(Tensor accXY) {
    synchronized (boundedLinkedList) {
      boundedLinkedList.add(geodesicIIR1Filter.apply(accXY.map(Magnitude.ACCELERATION)));
    }
  }
}
