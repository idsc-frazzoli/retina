// code by jph
package ch.ethz.idsc.retina.util.math;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Optional;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

public class Viewport {
  private static final Scalar NUM_ONE = DoubleScalar.of(1);

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
    half_width = width.multiply(RealScalar.of(0.5));
    half_height = height.multiply(RealScalar.of(0.5));
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
    if (Sign.isPositive(tensor.Get(3))) {
      Scalar xn = tensor.Get(0).divide(tensor.Get(3)).add(NUM_ONE).multiply(half_width);
      Scalar yn = NUM_ONE.subtract(tensor.Get(1).divide(tensor.Get(3))).multiply(half_height);
      return Optional.of(Tensors.of(xn, yn));
    }
    return Optional.empty();
  }

  public Optional<Point> toPixel(Tensor tensor) {
    if (Sign.isPositive(tensor.Get(3))) {
      double hx = tensor.Get(0).number().doubleValue();
      double hz = tensor.Get(3).number().doubleValue();
      double xn = (hx / hz + 1) * d_half_width;
      if (0 <= xn && xn < d_width) {
        double hy = tensor.Get(1).number().doubleValue();
        double yn = (int) ((1 - hy / hz) * d_half_height);
        if (0 <= yn && yn < d_height)
          return Optional.of(new Point((int) xn, (int) yn));
      }
    }
    return Optional.empty();
  }
}
