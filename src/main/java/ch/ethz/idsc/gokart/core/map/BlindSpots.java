// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Mod;

/** immutable */
public class BlindSpots {
  public static BlindSpots defaultGokart() {
    // TODO determine exact values
    Tensor angles = Tensors.of( //
        Tensors.vector(3., 3.4), //
        Tensors.vector(6.1, 0.2));
    return new BlindSpots(angles);
  }

  // ---
  private final List<Clip> list = new LinkedList<>();

  /** @param vector blind spot in azimuths [rad] */
  public BlindSpots(Tensor angles) {
    for (Tensor sector : angles.map(Mod.function(Pi.TWO))) {
      Scalar start = sector.Get(0);
      Scalar end = sector.Get(1);
      if (Scalars.lessEquals(start, end))
        list.add(Clips.interval(start, end));
      else {
        list.add(Clips.interval(start, Pi.TWO));
        list.add(Clips.interval(end.zero(), end));
      }
    }
  }

  /** @param azimuth in the interval [0, 2*pi] with interpretation in radian
   * @return whether the azimuth is in a blind spot */
  public boolean isBlind(Scalar azimuth) {
    return list.stream().anyMatch(clip -> clip.isInside(azimuth));
  }
}
