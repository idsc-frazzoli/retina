// code by gjoel
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPlanListener;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import ch.ethz.idsc.tensor.red.Nest;

public class ClothoidPlanRender extends PathRender implements ClothoidPlanListener {
  private final static int REFINEMENT = 5;

  public ClothoidPlanRender(Color color) {
    super(color);
  }

  @Override // from ClothoidPlanListener
  public void planReceived(ClothoidPlan clothoidPlan) {
    // TODO GJOEL curve is already refined
    setCurve(Nest.of(Clothoid3.CURVE_SUBDIVISION::string, clothoidPlan.curve(), REFINEMENT), false);
  }
}
