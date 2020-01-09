// code by mh, jph, ta
package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

// TODO JPH class design, see where Limit is used, possibly hide more
/* package */ class Limit {
  Scalar lo;
  Scalar hi;
}

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
    Scalar init = shift.zero();
    { // find free space
      boolean occupied = true;
      while (occupied) {
        if (Sign.isNegative(init))
          init = init.negate();
        else
          init = init.add(increment).negate();
        occupied = region.isMember(pos.add(dir.multiply(init)));
        if (Scalars.lessThan(shift, init.abs())) {
          init = shift.zero();
          // initial implementation gave up here:
          // perhaps make depend on "occupied"
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
      limit.lo = probe.add(increment);//Want the distance before collision
    }
    { // positive direction
      Scalar probe = init;
      while (Scalars.lessThan(probe.abs(), max)) {
        probe = probe.add(increment);
        if (region.isMember(pos.add(dir.multiply(probe))))
          break;
      }
      limit.hi = probe.subtract(increment);//Want the distance before collision
    }
    return limit;
  }
}
