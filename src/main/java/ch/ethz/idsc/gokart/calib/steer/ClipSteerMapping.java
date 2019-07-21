// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.io.Serializable;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;

public class ClipSteerMapping implements SteerMapping, Serializable {
  public static SteerMapping wrap(SteerMapping steerMapping, Clip ratioClip) {
    return new ClipSteerMapping(steerMapping, ratioClip);
  }

  // ---
  private final SteerMapping steerMapping;
  private final Clip ratioClip;

  public ClipSteerMapping(SteerMapping steerMapping, Clip ratioClip) {
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
