// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.gokart.core.mpc.MPCInformationProvider;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class MPCPredictionRender implements RenderInterface {
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor positions = MPCInformationProvider.getInstance().getPositions();
    if (!Tensors.isEmpty(positions)) {
      // draw
      graphics.setColor(Color.GREEN);
      Path2D path2d = geometricLayer.toPath2D(positions);
      graphics.draw(path2d);
    }
  }
}
