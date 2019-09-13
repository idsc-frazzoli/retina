// code by mh, jph
package ch.ethz.idsc.gokart.core.track;

import java.awt.Graphics2D;
import java.util.Optional;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

/** receives BSplineTrack and renders lane center line and boundaries using {@link TrackRender} */
public class BSplineTrackRender implements RenderInterface, BSplineTrackListener {
  private final TrackRender trackRender = new TrackRender();

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    trackRender.render(geometricLayer, graphics);
  }

  @Override // from BSplineTrackListener
  public void bSplineTrack(Optional<BSplineTrack> optional) {
    trackRender.setTrack(optional.orElse(null));
  }
}
