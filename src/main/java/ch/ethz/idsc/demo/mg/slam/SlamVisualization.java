// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

public class SlamVisualization implements RenderInterface {
  private GeometricLayer geometricLayer; // use GeometricLayer for visualization

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // code below from BayesianOccupancyGrid, might implement similar solution
    // Tensor model2pixel = geometricLayer.getMatrix();
    // Tensor translate = IdentityMatrix.of(3);
    // translate.set(lbounds.get(0).multiply(cellDimInv), 0, 2);
    // translate.set(lbounds.get(1).multiply(cellDimInv), 1, 2);
    // Tensor matrix = model2pixel.dot(scaling).dot(translate);
    // graphics.drawImage(obstacleImage, AffineTransforms.toAffineTransform(matrix), null);
  }
}
