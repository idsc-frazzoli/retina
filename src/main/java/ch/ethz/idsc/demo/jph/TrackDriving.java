// code by jph
package ch.ethz.idsc.demo.jph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.sca.Ramp;

public class TrackDriving implements RenderInterface {
  private static final ColorDataIndexed COLOR_DATA_INDEXED1 = ColorDataLists._063.cyclic();
  private static final ColorDataIndexed COLOR_DATA_INDEXED2 = COLOR_DATA_INDEXED1.deriveWithAlpha(128);
  private static final Tensor ARROWHEAD = Arrowhead.of(0.6);
  // ---
  private final Tensor tensor;
  private final int id;
  private final int offset;

  public TrackDriving(Tensor tensor, int id) {
    this.tensor = tensor;
    this.id = id;
    offset = IntStream.range(0, tensor.length()) //
        .filter(index -> Scalars.lessThan(RealScalar.of(1000), tensor.Get(index, 1))) //
        .findFirst().getAsInt();
  }

  public Tensor row(int index) {
    index += offset;
    index = Math.min(tensor.length() - 1, index);
    return tensor.get(index);
  }

  public int maxIndex() {
    return tensor.length() - offset;
  }

  int render_index;

  public void setRenderIndex(int render_index) {
    this.render_index = render_index;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor row = row(render_index);
    Tensor xya = row.extract(10, 13); // unitless
    Tensor matrix = Se2Utils.toSE2Matrix(xya);
    geometricLayer.pushMatrix(matrix);
    {
      Path2D path2d = geometricLayer.toPath2D(ARROWHEAD);
      graphics.setColor(COLOR_DATA_INDEXED2.getColor(id));
      graphics.fill(path2d);
      graphics.setColor(COLOR_DATA_INDEXED1.getColor(id));
      graphics.draw(path2d);
    }
    graphics.setStroke(new BasicStroke(1.5f));
    {
      Scalar factor = row.Get(1).divide(RealScalar.of(4000));
      Path2D path2d = geometricLayer.toPath2D(Tensors.of(Array.zeros(2), UnitVector.of(2, 0).multiply(factor)));
      graphics.setColor(Color.BLUE);
      graphics.draw(path2d);
    }
    {
      Scalar factor = Ramp.FUNCTION.apply(row.Get(9).negate().subtract(RealScalar.of(0.02))).divide(RealScalar.of(-0.06));
      Path2D path2d = geometricLayer.toPath2D(Tensors.of(Array.zeros(2), UnitVector.of(2, 0).multiply(factor)));
      graphics.setColor(Color.RED);
      graphics.draw(path2d);
    }
    graphics.setStroke(new BasicStroke(1));
    geometricLayer.popMatrix();
  }
}
