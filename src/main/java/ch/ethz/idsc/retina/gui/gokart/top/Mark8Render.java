// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owly.gui.GeometricLayer;
import ch.ethz.idsc.owly.gui.RenderInterface;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;

class Mark8Render implements RenderInterface, LidarRayBlockListener {
  private LidarRayBlockEvent _lidarRayBlockEvent;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(_lidarRayBlockEvent)) {
      LidarRayBlockEvent lidarRayBlockEvent = _lidarRayBlockEvent;
      System.out.println("here " + lidarRayBlockEvent.size());
      // lidarRayBlockEvent.floatBuffer.limit();
    }
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    _lidarRayBlockEvent = lidarRayBlockEvent;
  }
}
