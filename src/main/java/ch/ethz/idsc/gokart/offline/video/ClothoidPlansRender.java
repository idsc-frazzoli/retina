package ch.ethz.idsc.gokart.offline.video;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPlanListener;
import ch.ethz.idsc.owl.gui.ColorLookup;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.util.BoundedLinkedList;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;

/* package */ class ClothoidPlansRender implements ClothoidPlanListener, RenderInterface {
  private final BoundedLinkedList<ClothoidPlan> boundedLinkedList;
  private final ColorDataIndexed colorDataIndexed;

  public ClothoidPlansRender(int limit) {
    boundedLinkedList = new BoundedLinkedList<>(limit);
    colorDataIndexed = ColorLookup.decreasing(limit, ColorDataGradients.ALPINE).deriveWithAlpha(128);
  }

  @Override
  public void planReceived(ClothoidPlan clothoidPlan) {
    synchronized (boundedLinkedList) {
      boundedLinkedList.add(clothoidPlan);
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    synchronized (boundedLinkedList) {
      graphics.setStroke(new BasicStroke(1));
      int index = -1;
      for (ClothoidPlan clothoidPlan : boundedLinkedList) {
        Path2D path2d = geometricLayer.toPath2D(clothoidPlan.curve());
        graphics.setColor(colorDataIndexed.getColor(++index));
        graphics.draw(path2d);
      }
    }
  }
}
