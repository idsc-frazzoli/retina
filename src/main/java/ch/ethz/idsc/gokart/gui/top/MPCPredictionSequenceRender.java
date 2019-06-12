// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.core.mpc.MPCControlUpdateListener;
import ch.ethz.idsc.owl.gui.ColorLookup;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.util.BoundedLinkedList;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;

public class MPCPredictionSequenceRender implements MPCControlUpdateListener, RenderInterface {
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final ColorDataIndexed colorDataIndexed;

  public MPCPredictionSequenceRender(int limit) {
    boundedLinkedList = new BoundedLinkedList<>(limit);
    colorDataIndexed = ColorLookup.decreasing(limit, ColorDataGradients.ALPINE).deriveWithAlpha(128);
  }

  @Override
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    boundedLinkedList.add(controlAndPredictionSteps.toPositions());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    int index = -1;
    for (Tensor positions : boundedLinkedList) {
      graphics.setColor(colorDataIndexed.getColor(++index));
      graphics.draw(geometricLayer.toPath2D(positions));
    }
  }
}
