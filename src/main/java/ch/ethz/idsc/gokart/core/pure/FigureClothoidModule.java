// code by jph, gjoel
package ch.ethz.idsc.gokart.core.pure;

/** listens to curves for pursuit */
public class FigureClothoidModule extends AbstractFigureModule {
  public FigureClothoidModule() {
    super(new CurveClothoidPursuitModule(ClothoidPursuitConfig.GLOBAL));
  }
}
