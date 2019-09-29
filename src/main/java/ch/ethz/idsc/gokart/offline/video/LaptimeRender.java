// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

public class LaptimeRender implements RenderInterface {
  private final BSplineTrack bSplineTrack;

  public LaptimeRender(BSplineTrack bSplineTrack) {
    this.bSplineTrack = bSplineTrack;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO Auto-generated method stub
  }
}
