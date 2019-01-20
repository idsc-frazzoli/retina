// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.map.GokartTrackIdentificationModule;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;

public class LiveTrackRenderProvider implements RenderInterface {
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GokartTrackIdentificationModule gokartTrackIdentificationModule = //
        ModuleAuto.INSTANCE.getInstance(GokartTrackIdentificationModule.class);
    if (Objects.nonNull(gokartTrackIdentificationModule))
      gokartTrackIdentificationModule.render(geometricLayer, graphics);
  }
}
