// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.core.mpc.MPCControlUpdateListener;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class MPCPredictionRender extends MPCControlUpdateListener implements RenderInterface {
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (cns != null) {
      // create line
      Tensor predictionLine = Tensors.empty();
      for (int i = 0; i < cns.length(); i++) {
        predictionLine.append(//
            Tensors.of(//
                cns.steps[i].state.getUx(), //
                cns.steps[i].state.getUy()));
      }
      // draw
      graphics.setColor(Color.GREEN);
      Path2D path2d = geometricLayer.toPath2D(predictionLine);
      graphics.draw(path2d);
    }
  }
}
