// code by mh
package ch.ethz.idsc.gokart.core.map;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;

public class LiveTrackRenderProvider implements RenderInterface {
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GokartTrackReconModule gokartTrackReconModule = //
        ModuleAuto.INSTANCE.getInstance(GokartTrackReconModule.class);
    if (Objects.nonNull(gokartTrackReconModule))
      gokartTrackReconModule.render(geometricLayer, graphics);
  }
}
