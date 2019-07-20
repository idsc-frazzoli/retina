// code by mh, jph
package ch.ethz.idsc.gokart.core.map;

import java.awt.Graphics2D;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.gui.top.TrackRender;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

/** receives MPCBSplineTrack and renders lane center line and boundaries using {@link TrackRender} */
public class MPCBSplineTrackRender implements RenderInterface, MPCBSplineTrackListener {
  private final TrackRender trackRender = new TrackRender();

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    trackRender.render(geometricLayer, graphics);
  }

  @Override // from MPCBSplineTrackListener
  public void mpcBSplineTrack(Optional<MPCBSplineTrack> optional) {
    TrackInterface trackInterface = optional.map(MPCBSplineTrack::bSplineTrack).orElse(null);
    trackRender.setTrack(trackInterface);
  }
}
