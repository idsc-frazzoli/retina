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
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

/* package */ class GroundSpeedRender implements GokartPoseListener, RenderInterface {
  private static final Stroke STROKE_DEFAULT = new BasicStroke();
  private static final Tensor ORIGIN = Array.zeros(2);
  private static final Scalar SCALE = RealScalar.of(0.1);
  // ---
  private final Tensor xya;
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();

  public GroundSpeedRender(Tensor xya) {
    this.xya = xya;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor line = Tensors.of(ORIGIN, gokartPoseEvent.getVelocityXY().multiply(SCALE));
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    graphics.setColor(Color.BLUE);
    graphics.setStroke(new BasicStroke(geometricLayer.model2pixelWidth(0.03)));
    GraphicsUtil.setQualityHigh(graphics);
    graphics.draw(geometricLayer.toPath2D(line));
    GraphicsUtil.setQualityDefault(graphics);
    graphics.setStroke(STROKE_DEFAULT);
    geometricLayer.popMatrix();
  }
}
