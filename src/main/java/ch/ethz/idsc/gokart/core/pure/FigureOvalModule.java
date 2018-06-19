// code by jph
package ch.ethz.idsc.gokart.core.pure;

public class FigureOvalModule extends FigureBaseModule {
  /** until 20180226 the curve for trajectory pursuit was
   * DubendorfCurve.OVAL
   * 
   * due to new safety structure, the curve made a bit smaller and shifted slightly
   * in the direction away from the container. the new curve is
   * DubendorfCurve.OVAL_SHIFTED
   * 
   * then the hyperloop project was introduced to the hanger which further reduced
   * the operating domain for the gokart. */
  public FigureOvalModule() {
    super(DubendorfCurve.HYPERLOOP_OVAL);
  }
}
