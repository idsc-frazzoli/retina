// code by jph
package ch.ethz.idsc.retina.util.math;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

/** @see AxisAlignedBox */
// TODO JPH can be made more efficient
public class CurvedBar implements Serializable {
  private static final int RESOLUTION = 10;
  // ---
  private final Scalar lo;
  private final Scalar hi;

  public CurvedBar(Scalar radius, Scalar width) {
    Scalar semi = width.multiply(RealScalar.of(0.5));
    this.lo = radius.subtract(semi);
    this.hi = radius.add(semi);
  }

  public Tensor single(Scalar value) {
    return span(Sign.isPositiveOrZero(value) //
        ? Clips.interval(value.zero(), value)
        : Clips.interval(value, value.zero()));
  }

  public Tensor span(Clip clip) {
    int steps = (int) Math.max(1, Math.ceil(clip.width().number().doubleValue() * RESOLUTION));
    Tensor angles = Subdivide.of(clip.min(), clip.max(), steps);
    Tensor rays = angles.map(AngleVector::of);
    return Join.of(rays.multiply(lo), Reverse.of(rays.multiply(hi)));
  }
}
