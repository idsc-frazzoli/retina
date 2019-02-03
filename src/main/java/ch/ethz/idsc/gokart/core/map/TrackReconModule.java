// code by mh
package ch.ethz.idsc.gokart.core.map;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public final class TrackReconModule extends AbstractClockedModule implements GokartPoseListener {
  /** TODO JPH magic const */
  private static final Scalar PERIOD = Quantity.of(0.5, SI.SECOND);
  // ---
  protected final TimerFrame timerFrame = new TimerFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final TrackMapping trackMapping;
  private final TrackReconManagement trackReconManagement;
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final IntervalClock intervalClock = new IntervalClock();
  private final List<MPCBSplineTrackListener> listeners = new CopyOnWriteArrayList<>();
  // ---
  private GokartPoseEvent gokartPoseEvent = null;
  private boolean flagStart = false;
  private TrackReconMode trackReconMode = TrackReconMode.PASSIVE_SEND_LAST;
  private Optional<MPCBSplineTrack> lastTrack = Optional.empty();
  private final TrackReconRender trackReconRender = new TrackReconRender();

  public TrackReconModule() {
    trackMapping = new TrackMapping();
    trackReconManagement = new TrackReconManagement(trackMapping);
  }

  public TrackMapping trackMapping() {
    return trackMapping;
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    {
      timerFrame.geometricComponent.addRenderInterface(trackMapping);
      listenersAdd(trackReconRender);
      timerFrame.geometricComponent.addRenderInterface(trackReconRender);
    }
    {
      JButton jButton = new JButton("reset & flag start");
      jButton.addActionListener(actionEvent -> resetFlagStart());
      timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("reset track");
      jButton.addActionListener(actionEvent -> resetTrack());
      timerFrame.jToolBar.add(jButton);
    }
    {
      SpinnerLabel<TrackReconMode> spinnerLabel = new SpinnerLabel<>();
      spinnerLabel.setArray(TrackReconMode.values());
      spinnerLabel.setIndex(2);
      spinnerLabel.addSpinnerListener(this::setMode);
      spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 26), "");
    }
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    trackMapping.start();
    windowConfiguration.attach(getClass(), timerFrame.jFrame);
    timerFrame.configCoordinateOffset(400, 500);
    timerFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        private_windowClosed();
      }
    });
    timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    timerFrame.jFrame.setVisible(true);
  }

  private void private_windowClosed() {
    // ---
  }

  @Override // from AbstractModule
  protected void last() {
    timerFrame.close();
    trackMapping.stop();
    listenersRemove(trackReconRender);
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

  public static void main(String[] args) throws Exception {
    TrackReconModule trackReconModule = new TrackReconModule();
    trackReconModule.first();
    trackReconModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
