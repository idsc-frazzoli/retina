// code by jph, gjoel
package ch.ethz.idsc.demo.jg.following;

import ch.ethz.idsc.gokart.core.pure.AbstractFigureModule;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitModule;
import ch.ethz.idsc.gokart.core.pure.PursuitConfig;

/** listens to curves for pursuit */
public class FigureClothoidModule extends AbstractFigureModule {
  public FigureClothoidModule() {
    super(new CurveClothoidPursuitModule(PursuitConfig.GLOBAL));
  }
}
