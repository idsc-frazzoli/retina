// code by mh
package ch.ethz.idsc.gokart.core.map;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.lab.TrackIdentificationButtons;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public class GokartTrackIdentificationModule extends AbstractClockedModule //
    implements GokartPoseListener, RenderInterface {
  public static MPCBSplineTrack TRACK = null;
  // ---
  private final TrackIdentificationManagement trackIDManagement;
  private final GokartTrackMappingModule trackMappingModule;
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  // ---
  private GokartPoseEvent gokartPoseEvent = null;
  private Timing lastExecution = Timing.started();

  public GokartTrackIdentificationModule() {
    trackMappingModule = new GokartTrackMappingModule();
    trackIDManagement = new TrackIdentificationManagement(trackMappingModule);
  }

  public void resetTrack() {
    trackIDManagement.resetTrack();
  }

  @Override
  protected void runAlgo() {
    if (!trackIDManagement.isStartSet() || TrackIdentificationButtons.SETTINGSTART) {
      trackIDManagement.setStart(gokartPoseEvent);
      if (trackIDManagement.isStartSet()) {
        System.out.println("start set!");
        TrackIdentificationButtons.SETTINGSTART = false;
      }
    }
    if (TrackIdentificationButtons.RECORDING) {
      trackMappingModule.prepareMap();
      TRACK = trackIDManagement.update(gokartPoseEvent, Quantity.of(lastExecution.seconds(), SI.SECOND));
    }
    lastExecution = Timing.started();
  }

  @Override
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    trackMappingModule.start();
  }

  @Override
  protected void last() {
    trackMappingModule.stop();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(1, SI.SECOND);
  }

  @Override
  public void getEvent(GokartPoseEvent gpe) {
    this.gokartPoseEvent = gpe;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    trackIDManagement.render(geometricLayer, graphics);
  }
}
