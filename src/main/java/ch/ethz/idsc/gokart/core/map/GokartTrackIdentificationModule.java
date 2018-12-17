package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.TrackIdentificationManagement;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class GokartTrackIdentificationModule extends AbstractClockedModule implements GokartPoseListener {
  TrackIdentificationManagement trackIDManagement;
  GokartTrackMappingModule trackMappingModule;
  GokartPoseEvent gpe = null;
  Stopwatch lastExecution = Stopwatch.started();
  
  public static MPCBSplineTrack TRACK = null;
  public static RenderInterface trackIdenficationRender = null;
  public static RenderInterface trackMappingRender = null;

  public GokartTrackIdentificationModule() {
    trackMappingModule = new GokartTrackMappingModule();
    trackIDManagement = new TrackIdentificationManagement(trackMappingModule);
    trackIdenficationRender = trackIDManagement;
    trackMappingRender = trackMappingModule;
  }

  @Override
  protected void runAlgo() {
    while (!trackIDManagement.isStartSet()) {
      trackIDManagement.setStart(gpe);
    }
    trackMappingModule.prepareMap();
    TRACK = trackIDManagement.update(gpe, Quantity.of(lastExecution.display_seconds(), SI.SECOND));
    lastExecution = Stopwatch.started();
  }

  @Override
  protected void first() throws Exception {
    trackMappingModule.start();
  }

  @Override
  protected void last() {
    trackMappingModule.stop();
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(0.5, SI.SECOND);
  }

  @Override
  public void getEvent(GokartPoseEvent gpe) {
    this.gpe = gpe;
  }
}
