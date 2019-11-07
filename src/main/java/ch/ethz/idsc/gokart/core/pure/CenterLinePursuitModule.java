// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.core.track.BSplineTrackLcmClient;
import ch.ethz.idsc.gokart.core.track.BSplineTrackListener;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;

/** module requires the TrackReconModule to provide the center line of an identified track */
public class CenterLinePursuitModule extends AbstractModule implements BSplineTrackListener {
  /** in dubendorf resolution 100 yields points approx 0.5[m] apart.
   * resolution = 200 results in a spacing of ~0.25[m] */
  private static final int RESOLUTION = 200;
  // ---
  private final List<BSplineTrackLcmClient> bSplineTrackLcmClients = Arrays.asList( //
      BSplineTrackLcmClient.string(), //
      BSplineTrackLcmClient.cyclic());
  private final CurvePursuitModule curvePurePursuitModule = new CurvePurePursuitModule(PurePursuitConfig.GLOBAL);

  @Override
  protected void first() {
    bSplineTrackLcmClients.forEach(bSplineTrackLcmClient -> bSplineTrackLcmClient.addListener(this));
    bSplineTrackLcmClients.forEach(BSplineTrackLcmClient::startSubscriptions);
    curvePurePursuitModule.launch();
  }

  @Override
  protected void last() {
    curvePurePursuitModule.terminate();
    bSplineTrackLcmClients.forEach(BSplineTrackLcmClient::stopSubscriptions);
  }

  @Override // from BSplineTrackListener
  public void bSplineTrack(Optional<BSplineTrack> optional) {
    Tensor curve = null;
    boolean closed = true;
    if (optional.isPresent()) {
      BSplineTrack bSplineTrack = optional.get();
      curve = bSplineTrack.getLineMiddle(RESOLUTION).map(Magnitude.METER);
      closed = bSplineTrack.isClosed();
      System.out.println("updated curve " + Dimensions.of(curve) + " closed=" + closed);
    } else {
      System.out.println("center line no waypoints");
    }
    curvePurePursuitModule.setCurve(Optional.ofNullable(curve), closed);
    // TODO JPH publish
  }
}
