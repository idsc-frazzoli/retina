// code by mh, jph
package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class Limit {
  Scalar lo;
  Scalar hi;
}

// TODO JPH class contains magic constants
/* package */ class RegionRayTrace {
  private final Region<Tensor> region;
  private final Scalar increment;
  private final Scalar shift;
  private final Scalar max;

  public RegionRayTrace(Region<Tensor> region, Scalar increment, Scalar shift, Scalar max) {
    this.region = region;
    this.increment = increment;
    this.shift = shift;
    this.max = max;
  }

  public Limit getLimits(final Tensor pos, final Tensor dir) {
    Limit limit = new Limit();
    // find free space
    Scalar init = Quantity.of(-0.001, SI.METER);
    {
      boolean occupied = true;
      while (occupied) {
        if (Sign.isNegative(init))
          init = init.negate(); // TODO JPH whut?
        else
          init = init.add(increment).negate();
        Tensor element = pos.add(dir.multiply(init));
        occupied = region.isMember(element);
        if (Scalars.lessThan(shift, init.abs())) {
          // TODO JPH why not break here
          // return Optional.empty();
          break;
        }
      }
    }
    { // negative direction
      Scalar probe = init;
      while (Scalars.lessThan(probe.abs(), max)) {
        probe = probe.subtract(increment);
        if (region.isMember(pos.add(dir.multiply(probe))))
          break;
      }
      limit.lo = probe;
    }
    { // positive direction
      Scalar probe = init;
      while (Scalars.lessThan(probe.abs(), max)) {
        probe = probe.add(increment);
        if (region.isMember(pos.add(dir.multiply(probe))))
          break;
      }
      limit.hi = probe;
    }
    return limit;
  }
}
