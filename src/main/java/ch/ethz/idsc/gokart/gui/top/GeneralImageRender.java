// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class GeneralImageRender implements RenderInterface {
  private final BufferedImage bufferedImage;
  private final Tensor matrix;

  /** @param bufferedImage
   * @param scale vector of length 2 */
  public GeneralImageRender(BufferedImage bufferedImage, Tensor model2pixel) {
    this.bufferedImage = bufferedImage;
    this.matrix = model2pixel;
    // VectorQ.requireLength(scale, 2);
    // Tensor weights = Tensors.of(scale.Get(0).reciprocal(), scale.Get(1).reciprocal().negate(), RealScalar.ONE);
    // Tensor translate = Se2Utils.toSE2Translation(Tensors.vector(0, -bufferedImage.getHeight()));
    // matrix = weights.pmul(translate);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.WHITE);
    graphics.drawRenderedImage(bufferedImage, //
        AffineTransforms.toAffineTransform(geometricLayer.getMatrix().dot(matrix)));
  }
}
