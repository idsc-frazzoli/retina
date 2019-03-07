// code by jph
package ch.ethz.idsc.demo.jph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.sca.Ramp;

/* package */ class TrackDriving implements RenderInterface {
  private static final ColorDataIndexed COLOR_DATA_INDEXED1 = ColorDataLists._063.cyclic().deriveWithAlpha(128);
  private static final ColorDataIndexed COLOR_DATA_INDEXED2 = COLOR_DATA_INDEXED1.deriveWithAlpha(32);
  private static final Tensor FOOTPRINT = RimoSinusIonModel.standard().footprint();
  // Arrowhead.of(0.6);
  // ---
  private final Tensor tensor;
  private final int id;
  private final int offset;
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

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

  public void setDriver(String name) {
    Tensor image = ResourceData.of("/image/driver/" + name + ".png");
    if (Objects.nonNull(image))
      bufferedImage = ImageFormat.of(image);
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
      Path2D path2d = geometricLayer.toPath2D(FOOTPRINT);
      path2d.closePath();
      graphics.setColor(COLOR_DATA_INDEXED2.getColor(id));
      graphics.fill(path2d);
      graphics.setColor(COLOR_DATA_INDEXED1.getColor(id));
      graphics.draw(path2d);
    }
    {
      Point2D point2d = geometricLayer.toPoint2D(Array.zeros(2));
      graphics.drawImage(bufferedImage, //
          (int) point2d.getX() - 16, //
          (int) point2d.getY() - 16, 32, 32, null);
    }
    graphics.setStroke(new BasicStroke(2.5f));
    {
      Scalar factor = row.Get(1).divide(RealScalar.of(4000));
      graphics.setColor(new Color(0, 0, 255, 128));
      graphics.draw(geometricLayer.toVector(Tensors.vector(1, 0), UnitVector.of(2, 0).multiply(factor)));
    }
    {
      Scalar factor = Ramp.FUNCTION.apply(row.Get(9).negate().subtract(RealScalar.of(0.02))).divide(RealScalar.of(-0.06));
      graphics.setColor(new Color(255, 0, 0, 128));
      graphics.draw(geometricLayer.toVector(Tensors.vector(1, 0), UnitVector.of(2, 0).multiply(factor)));
    }
    graphics.setStroke(new BasicStroke(2.5f));
    {
      // SteerConfig.GLOBAL.column2steer == 0.6[...]
      Scalar angle = row.Get(8).multiply(RealScalar.of(0.6)); // angle
      Tensor pair = ChassisGeometry.GLOBAL.getAckermannSteering().pair(angle);
      graphics.setColor(new Color(128, 128, 128, 128));
      Tensor v1 = AngleVector.of(pair.Get(0)).multiply(RealScalar.of(.2));
      graphics.draw(geometricLayer.toVector(Tensors.vector(1.19, +.5), v1));
      graphics.draw(geometricLayer.toVector(Tensors.vector(1.19, +.5), v1.negate()));
      Tensor v2 = AngleVector.of(pair.Get(1)).multiply(RealScalar.of(.2));
      graphics.draw(geometricLayer.toVector(Tensors.vector(1.19, -.5), v2));
      graphics.draw(geometricLayer.toVector(Tensors.vector(1.19, -.5), v2.negate()));
    }
    graphics.setStroke(new BasicStroke(1));
    geometricLayer.popMatrix();
  }

  public Scalar timeFor(int index) {
    return row(index).Get(0).subtract(row(0).Get(0));
  }
}
