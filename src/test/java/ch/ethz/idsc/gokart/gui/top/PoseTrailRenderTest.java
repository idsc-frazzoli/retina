// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class PoseTrailRenderTest extends TestCase {
  public void testSimple() {
    PoseTrailRender poseTrailRender = new PoseTrailRender();
    poseTrailRender.getEvent(GokartPoseEvents.offlineV1(Tensors.fromString("{1[m], 2[m], 3}"), RealScalar.ONE));
    poseTrailRender.getEvent(GokartPoseEvents.offlineV1(Tensors.fromString("{1.1[m], 2.0[m], 3.1}"), RealScalar.ONE));
    poseTrailRender.getEvent(GokartPoseEvents.offlineV1(Tensors.fromString("{1.1[m], 2.1[m], 3.1}"), RealScalar.ONE));
    poseTrailRender.render( //
        GeometricLayer.of(IdentityMatrix.of(3)), //
        new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR).createGraphics());
  }
}
