// code by jph
package ch.ethz.idsc.gokart.core.pure;

/** listens to curves for pursuit */
public class FigurePureModule extends AbstractFigureModule {
  public FigurePureModule() {
    super(new CurvePurePursuitModule(PursuitConfig.GLOBAL));
  }
}
