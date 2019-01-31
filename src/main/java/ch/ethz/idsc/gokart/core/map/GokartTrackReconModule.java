// code by mh
package ch.ethz.idsc.gokart.core.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public final class GokartTrackReconModule extends AbstractClockedModule implements GokartPoseListener {
  /** TODO JPH magic const */
  private static final Scalar PERIOD = Quantity.of(0.5, SI.SECOND);
  // ---
  private final TrackMapping trackMapping;
  private final TrackReconManagement trackReconManagement;
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final IntervalClock intervalClock = new IntervalClock();
  private final List<MPCBSplineTrackListener> listeners = new CopyOnWriteArrayList<>();
  // ---
  private GokartPoseEvent gokartPoseEvent = null;
  private boolean flagStart = true;
  private TrackReconMode trackReconMode = TrackReconMode.PASSIVE_SEND_LAST;
  private Optional<MPCBSplineTrack> lastTrack = Optional.empty();

  public GokartTrackReconModule() {
    trackMapping = new TrackMapping();
    trackReconManagement = new TrackReconManagement(trackMapping);
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    trackMapping.start();
  }

  @Override // from AbstractModule
  protected void last() {
    trackMapping.stop();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    if (Objects.isNull(gokartPoseEvent))
      return;
    // ---
    if (flagStart && !trackReconManagement.isStartSet()) {
      trackReconManagement.setStart(gokartPoseEvent);
      if (trackReconManagement.isStartSet()) {
        System.out.println("start set!");
        flagStart = false;
      } else {
        System.err.println("start NOT set");
      }
    }
    double seconds = intervalClock.seconds(); // reset
    if (trackReconMode.isActive()) {
      trackMapping.prepareMap();
      lastTrack = trackReconManagement.update(gokartPoseEvent, Quantity.of(seconds, SI.SECOND));
    }
    // ---
    Optional<MPCBSplineTrack> sendTrack = trackReconMode.isSendLast() //
        ? lastTrack
        : Optional.empty();
    listeners.forEach(listener -> listener.mpcBSplineTrack(sendTrack));
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return PERIOD;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  /** reset track and flag start at current pose */
  public void resetFlagStart() {
    trackReconManagement.resetTrack();
    flagStart = true;
  }

  /** reset track */
  public void resetTrack() {
    trackReconManagement.resetTrack();
  }

  public void setMode(TrackReconMode trackReconMode) {
    this.trackReconMode = trackReconMode;
  }

  public void listenersAdd(MPCBSplineTrackListener mpcBSplineTrackListener) {
    listeners.add(mpcBSplineTrackListener);
  }

  public void listenersRemove(MPCBSplineTrackListener mpcBSplineTrackListener) {
    boolean remove = listeners.remove(mpcBSplineTrackListener);
    if (!remove)
      new RuntimeException("not removed").printStackTrace();
  }
}
