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
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.N;

/** draws line of speed as well as brief history of velocities */
public class GroundSpeedRender extends CrosshairRender implements GokartPoseListener {
  /* package */ static final Color COLOR_VELOCITY = new Color(200, 67, 255);
  private static final Stroke STROKE_DEFAULT = new BasicStroke();
  private static final Tensor ORIGIN = Array.zeros(2).map(N.DOUBLE);
  // ---
  private final Tensor matrix;
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();

  public GroundSpeedRender(int limit, Tensor matrix) {
    super(limit, ColorDataGradients.ALPINE, Tensors.vector(5, 10));
    this.matrix = matrix;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
    push_back(velXY(gokartPoseEvent));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(matrix);
    GraphicsUtil.setQualityHigh(graphics);
    // ---
    renderCrosshairTrace(geometricLayer, graphics);
    // ---
    graphics.setColor(COLOR_VELOCITY);
    graphics.setStroke(new BasicStroke(geometricLayer.model2pixelWidth(0.25)));
    graphics.draw(geometricLayer.toPath2D(Tensors.of(ORIGIN, velXY(gokartPoseEvent))));
    GraphicsUtil.setQualityDefault(graphics);
    graphics.setStroke(STROKE_DEFAULT);
    geometricLayer.popMatrix();
  }

  /** @param gokartPoseEvent
   * @return {vx, vy} without units */
  private static Tensor velXY(GokartPoseEvent gokartPoseEvent) {
    return gokartPoseEvent.getVelocity().extract(0, 2).map(Magnitude.VELOCITY);
  }
}
