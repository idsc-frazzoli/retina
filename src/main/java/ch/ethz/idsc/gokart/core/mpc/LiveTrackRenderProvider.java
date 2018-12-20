package ch.ethz.idsc.gokart.core.mpc;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.map.GokartTrackIdentificationModule;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

public class LiveTrackRenderProvider implements RenderInterface {
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if(GokartTrackIdentificationModule.TRACKIDENTIFICATION!=null) {
      GokartTrackIdentificationModule.TRACKIDENTIFICATION.render(geometricLayer, graphics);
    }
  }
}
