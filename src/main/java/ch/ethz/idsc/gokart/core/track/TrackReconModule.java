// code by mh
package ch.ethz.idsc.gokart.core.track;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.map.AbstractMapping;
import ch.ethz.idsc.gokart.core.map.ImageGrid;
import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.gokart.lcm.mod.BSplineTrackLcm;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO does not shut down properly in TaskTabbedGui when not closed separately
public final class TrackReconModule extends AbstractClockedModule implements GokartPoseListener {
  /** TODO JPH magic const */
  private static final Scalar PERIOD = Quantity.of(0.1, SI.SECOND);
  private static final RenderInterface GRID_RENDER = new GridRender(Subdivide.of(0, 100, 10));
  static final Tensor HANGAR_MODEL2PIXEL = Tensors.fromString("{{7.5*2, 0, -400}, {0, -7.5*2, 1050}, {0, 0, 1}}");
  // ---
  protected final TimerFrame timerFrame = new TimerFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final AbstractMapping<? extends ImageGrid> mapping = // SightLineMapping.defaultTrack();
      MappingConfig.GLOBAL.createTrackMapping();
  private final TrackReconManagement trackReconManagement;
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final IntervalClock intervalClock = new IntervalClock();
  private final List<MPCBSplineTrackListener> listeners = new CopyOnWriteArrayList<>();
  private final MPCBSplineTrackRender trackReconRender = new MPCBSplineTrackRender();
  private final GlobalViewLcmModule globalViewLcmModule = //
      ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);
  // ---
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  private boolean isActive = true;
  private Optional<MPCBSplineTrack> lastTrack = Optional.empty();

  public TrackReconModule() {
    trackReconManagement = new TrackReconManagement(mapping.getMap());
  }

  @Override // from AbstractModule
  protected void first() {
    timerFrame.geometricComponent.setModel2Pixel(HANGAR_MODEL2PIXEL);
    {
      timerFrame.geometricComponent.addRenderInterfaceBackground(GRID_RENDER);
      timerFrame.geometricComponent.addRenderInterface(mapping.getMap());
      listenersAdd(trackReconRender);
      timerFrame.geometricComponent.addRenderInterface(trackReconRender);
      timerFrame.geometricComponent.addRenderInterface(trackReconManagement.getTrackLayoutInitialGuess());
    }
    {
      if (Objects.nonNull(globalViewLcmModule))
        listenersAdd(globalViewLcmModule.trackReconRender);
    }
    {
      JButton jButton = new JButton("set start");
      jButton.addActionListener(actionEvent -> setStart());
      timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("compute track");
      jButton.addActionListener(actionEvent -> computeTrack());
      timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("export track");
      jButton.addActionListener(actionEvent -> exportTrack());
      timerFrame.jToolBar.add(jButton);
    }
    {
      JToggleButton jToggleButton = new JToggleButton("active");
      jToggleButton.setSelected(isActive);
      jToggleButton.addActionListener(actionEvent -> isActive = jToggleButton.isSelected());
      timerFrame.jToolBar.add(jToggleButton);
    }
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    mapping.start();
    windowConfiguration.attach(getClass(), timerFrame.jFrame);
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
    mapping.stop();
    listenersRemove(trackReconRender);
    if (Objects.nonNull(globalViewLcmModule)) {
      listenersRemove(globalViewLcmModule.trackReconRender);
      globalViewLcmModule.trackReconRender.mpcBSplineTrack(Optional.empty());
    }
    gokartPoseLcmClient.stopSubscriptions();
    BSplineTrackLcm.publish(Optional.empty());
    terminate();
  }

  @Override // from AbstractModule
  protected void last() {
    timerFrame.close();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    GokartPoseEvent _gokartPoseEvent = gokartPoseEvent;
    if (LocalizationConfig.GLOBAL.isQualityOk(_gokartPoseEvent)) {
      double seconds = intervalClock.seconds(); // reset
      if (isActive) {
        if (trackReconManagement.isStartSet()) {
          mapping.prepareMap();
          lastTrack = trackReconManagement.update(_gokartPoseEvent.getPose());
        } else
          System.out.println("no start set");
      }
      // ---
      listeners.forEach(listener -> listener.mpcBSplineTrack(lastTrack));
      BSplineTrackLcm.publish(lastTrack.map(MPCBSplineTrack::bSplineTrack));
    } else
      System.out.println("no quality pose");
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
  private void setStart() {
    GokartPoseEvent _gokartPoseEvent = gokartPoseEvent;
    if (LocalizationConfig.GLOBAL.isQualityOk(_gokartPoseEvent))
      trackReconManagement.setStart(_gokartPoseEvent.getPose());
    else
      System.out.println("no quality pose");
  }

  /** reset track */
  private void computeTrack() {
    trackReconManagement.computeTrack();
  }

  /** export track */
  private void exportTrack() {
    trackReconManagement.exportTrack();
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
    trackReconModule.launch();
    trackReconModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
