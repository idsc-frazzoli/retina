// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class GroundSpeedRender extends CrosshairRender implements GokartPoseListener {
  private static final Stroke STROKE_DEFAULT = new BasicStroke();
  private static final Tensor ORIGIN = Array.zeros(2);
  private static final Tensor DIAGONAL = DiagonalMatrix.of(.15, .15, 1);
  // ---
  private final Tensor xya;
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();

  public GroundSpeedRender(Tensor xya, int limit) {
    super(limit, ColorDataGradients.ALPINE, Tensors.vector(5, 10));
    this.xya = xya;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
    push_end(gokartPoseEvent.getVelocityXY().map(Magnitude.VELOCITY));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    geometricLayer.pushMatrix(DIAGONAL);
    super.render(geometricLayer, graphics);
    Tensor velocityXY = gokartPoseEvent.getVelocityXY();
    {
      GraphicsUtil.setQualityHigh(graphics);
      graphics.setColor(Color.DARK_GRAY);
      graphics.drawString("vel=" + velocityXY.map(Round._2), 0, 20);
      // ---
      graphics.setColor(new Color(200, 67, 255));
      graphics.setStroke(new BasicStroke(geometricLayer.model2pixelWidth(0.25)));
      graphics.draw(geometricLayer.toPath2D(Tensors.of(ORIGIN, velocityXY)));
      GraphicsUtil.setQualityDefault(graphics);
    }
    graphics.setStroke(STROKE_DEFAULT);
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
  }
}
