// code by gjoel
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPlanListener;
import ch.ethz.idsc.sophus.app.api.PathRender;

public class ClothoidPlanRender extends PathRender implements ClothoidPlanListener {
  public ClothoidPlanRender(Color color) {
    super(color);
  }

  @Override // from ClothoidPlanListener
  public void planReceived(ClothoidPlan clothoidPlan) {
    setCurve(clothoidPlan.curve(), false);
  }
}
