// code by mh, jph
package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Abs;

// TODO JPH class contains magic constants
/* package */ class RegionRayTrace {
  private final Region<Tensor> region;
  private final Scalar increment;
  private final Scalar max;

  public RegionRayTrace(Region<Tensor> region, Scalar increment, Scalar max) {
    this.region = region;
    this.increment = increment;
    this.max = max;
  }

  public Tensor getLimits(final Tensor pos, final Tensor dir) {
    // find free space
    Scalar sideStep = Quantity.of(-0.001, SI.METER);
    Tensor testPosition = null;
    Tensor lowPosition;
    Tensor highPosition;
    boolean occupied = true;
    while (occupied) {
      if (Scalars.lessThan(sideStep, Quantity.of(0, SI.METER)))
        sideStep = sideStep.negate();
      else
        sideStep = sideStep.add(increment).negate();
      testPosition = pos.add(dir.multiply(sideStep));
      occupied = region.isMember(testPosition);
      if (Scalars.lessThan(max, sideStep.abs())) {
        // TODO JPH why not break here
        return Tensors.of(RealScalar.ZERO, RealScalar.ZERO);
      }
    }
    // TODO JPH search in both directions for occupied cell
    // only for debugging
    // Tensor freeline = Tensors.empty();
    // negative direction
    while (!occupied && Scalars.lessThan(Abs.of(sideStep), Quantity.of(10, SI.METER))) {
      sideStep = sideStep.subtract(increment);
      testPosition = pos.add(dir.multiply(sideStep));
      occupied = region.isMember(testPosition);
    }
    // freeline.append(testPosition);
    lowPosition = sideStep;
    // negative direction
    occupied = false;
    while (!occupied && Scalars.lessThan(Abs.of(sideStep), Quantity.of(10, SI.METER))) {
      sideStep = sideStep.add(increment);
      testPosition = pos.add(dir.multiply(sideStep));
      occupied = region.isMember(testPosition);
    }
    highPosition = sideStep;
    // freeline.append(testPosition);
    // freeLines.add(freeline);
    return Tensors.of(lowPosition, highPosition);
  }
}
