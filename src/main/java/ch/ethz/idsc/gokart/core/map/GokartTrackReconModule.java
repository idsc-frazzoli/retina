// code by mh
package ch.ethz.idsc.gokart.core.map;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.time.IntervalClock;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public final class GokartTrackReconModule extends AbstractClockedModule implements GokartPoseListener, RenderInterface {
  private final TrackReconManagement trackReconManagement;
  private final TrackMapping trackMappingModule;
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final IntervalClock intervalClock = new IntervalClock();
  // ---
  private GokartPoseEvent gokartPoseEvent = null;
  private boolean flagStart = true;
  private MPCBSplineTrack mpcbSplineTrack = null;

  public GokartTrackReconModule() {
    trackMappingModule = new TrackMapping();
    trackReconManagement = new TrackReconManagement(trackMappingModule);
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    trackMappingModule.start();
  }

  @Override // from AbstractModule
  protected void last() {
    trackMappingModule.stop();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    if (Objects.isNull(gokartPoseEvent))
      return;
    // ---
    if (flagStart || !trackReconManagement.isStartSet()) {
      trackReconManagement.setStart(gokartPoseEvent);
      if (trackReconManagement.isStartSet()) {
        System.out.println("start set!");
        flagStart = false;
      }
    }
    double seconds = intervalClock.seconds(); // reset
    if (isRecording()) {
      trackMappingModule.prepareMap();
      mpcbSplineTrack = trackReconManagement.update(gokartPoseEvent, Quantity.of(seconds, SI.SECOND));
    }
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return Quantity.of(1, SI.SECOND);
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    trackReconManagement.render(geometricLayer, graphics);
  }

  public void resetTrack() {
    trackReconManagement.resetTrack();
  }

  public void setRecording(boolean selected) {
    trackMappingModule.setRecording(selected);
  }

  public boolean isRecording() {
    return trackMappingModule.isRecording();
  }

  public void flagStart() {
    flagStart = true;
  }

  public MPCBSplineTrack getMPCBSplineTrack() {
    return mpcbSplineTrack;
  }
}
