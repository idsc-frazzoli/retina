// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

@FunctionalInterface
public interface MPCBSplineTrackListener {
  void mpcBSplineTrack(Optional<MPCBSplineTrack> optional);
}
