// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class TrajectoryRenderTest extends TestCase {
  public void testSimple() {
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.render( //
        GeometricLayer.of(IdentityMatrix.of(3)), //
        new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR).createGraphics());
  }
}
