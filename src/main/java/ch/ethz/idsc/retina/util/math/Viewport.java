// code by jph
// http://www.thecodecrate.com/opengl-es/opengl-viewport-matrix/
package ch.ethz.idsc.retina.util.math;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

/** OpenGL style viewport transformation matrix */
public class Viewport {
  private static final Scalar ONE = RealScalar.of(1.0);
  private static final Scalar HALF = RealScalar.of(0.5);

  public static Viewport create(int width, int height) {
    return new Viewport(RealScalar.of(width), RealScalar.of(height));
  }

  public static Viewport create(Dimension dimension) {
    return create(dimension.width, dimension.height);
  }

  // ---
  private final Scalar width;
  private final Scalar height;
  private final Scalar half_width;
  private final Scalar half_height;
  private final double d_width;
  private final double d_height;
  private final double d_half_width;
  private final double d_half_height;

  private Viewport(Scalar width, Scalar height) {
    this.width = width;
    this.height = height;
    // ---
    half_width = width.multiply(HALF);
    half_height = height.multiply(HALF);
    // ---
    d_width = width.number().doubleValue();
    d_height = height.number().doubleValue();
    d_half_width = width.number().doubleValue() * 0.5;
    d_half_height = height.number().doubleValue() * 0.5;
  }

  public Scalar aspectRatio() {
    return width.divide(height);
  }

  public Optional<Tensor> fromProjected(Tensor tensor) {
    Scalar a = tensor.Get(3);
    if (Sign.isPositive(a)) {
      Scalar x = tensor.Get(0);
      Scalar y = tensor.Get(1);
      return Optional.of(Tensors.of( //
          x.divide(a).add(ONE).multiply(half_width), //
          ONE.subtract(y.divide(a)).multiply(half_height)));
    }
    return Optional.empty();
  }

  public Optional<Point> toPixel(Tensor tensor) {
    Scalar a = tensor.Get(3);
    if (Sign.isPositive(a)) {
      Scalar x = tensor.Get(0);
      Scalar y = tensor.Get(1);
      double hx = x.number().doubleValue();
      double hz = a.number().doubleValue();
      double xn = (hx / hz + 1) * d_half_width;
      if (0 <= xn && xn < d_width) {
        double hy = y.number().doubleValue();
        double yn = (int) ((1 - hy / hz) * d_half_height);
        if (0 <= yn && yn < d_height)
          return Optional.of(new Point((int) xn, (int) yn));
      }
    }
    return Optional.empty();
  }
}
