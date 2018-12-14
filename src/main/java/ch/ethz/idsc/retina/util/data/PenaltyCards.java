// code by jph
package ch.ethz.idsc.retina.util.data;

/** penalty starts when red card is issued
 * and lasts until yellow card is removed */
public final class PenaltyCards {
  private boolean isPenalty = false;

  /** Convention: red implies yellow
   * 
   * @param yellow
   * @param red */
  public void evaluate(boolean yellow, boolean red) {
    if (!yellow && red)
      System.err.println("wrong use of PenaltyCards API");
    isPenalty = isPenalty ? yellow : red;
  }

  public boolean isPenalty() {
    return isPenalty;
  }
}
