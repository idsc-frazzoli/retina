// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Graphics2D;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

/* package */ enum VoidLidarPointsRender implements LidarPointsRender {
  INSTANCE;
  // ---
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // ---
  }

  @Override
  public void getEvent(GokartPoseEvent getEvent) {
    // ---
  }

  @Override
  public void lasers(ByteBuffer byteBuffer) {
    // ---
  }
}
