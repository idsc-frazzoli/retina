// code by jph
package ch.ethz.idsc.gokart.core.track;

import java.util.Optional;

@FunctionalInterface
public interface BSplineTrackListener {
  /** @param optional that contains track if {@link TrackReconModule}
   * has established boundaries, else empty */
  void bSplineTrack(Optional<BSplineTrack> optional);
}
