// code by jph, gjoel
package ch.ethz.idsc.demo.jg.following;

import ch.ethz.idsc.gokart.core.pure.AbstractFigureModule;
import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitModule;

/** listens to curves for pursuit */
public class FigureClothoidModule extends AbstractFigureModule {
  public FigureClothoidModule() {
    super(new CurveClothoidPursuitModule(ClothoidPursuitConfig.GLOBAL));
  }
}
