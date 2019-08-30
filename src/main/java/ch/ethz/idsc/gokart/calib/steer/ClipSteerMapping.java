// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

public class ClipSteerMapping implements SteerMapping, Serializable {
  /** @param steerMapping
   * @param ratioClip [m^-1]
   * @return */
  public static SteerMapping wrap(SteerMapping steerMapping, Clip ratioClip) {
    ratioClip.requireInside(Quantity.of(0, SI.PER_METER));
    return new ClipSteerMapping( //
        Objects.requireNonNull(steerMapping), //
        ratioClip);
  }

  // ---
  private final SteerMapping steerMapping;
  private final Clip ratioClip;

  private ClipSteerMapping(SteerMapping steerMapping, Clip ratioClip) {
    this.steerMapping = steerMapping;
    this.ratioClip = ratioClip;
  }

  @Override // from SteerMapping
  public Scalar getRatioFromSCE(SteerColumnInterface steerColumnInterface) {
    return steerMapping.getRatioFromSCE(steerColumnInterface);
  }

  @Override // from SteerMapping
  public Scalar getRatioFromSCE(Scalar scalar) {
    return steerMapping.getRatioFromSCE(scalar);
  }

  @Override // from SteerMapping
  public Scalar getSCEfromRatio(Scalar ratio) {
    return steerMapping.getSCEfromRatio(ratioClip.apply(ratio));
  }
}
