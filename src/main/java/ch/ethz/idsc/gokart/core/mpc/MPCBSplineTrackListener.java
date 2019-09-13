// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.track.TrackReconModule;

/** TODO remove
 * replaced by {@link ch.ethz.idsc.gokart.core.track.BSplineTrackListener} */
@FunctionalInterface
public interface MPCBSplineTrackListener {
  /** @param optional that contains track if {@link TrackReconModule}
   * has established boundaries, else empty */
  void mpcBSplineTrack(Optional<MPCBSplineTrack> optional);
}
