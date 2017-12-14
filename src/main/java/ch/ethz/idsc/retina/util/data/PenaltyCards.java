// code by jph
package ch.ethz.idsc.retina.util.data;

public class PenaltyCards {
  private boolean isPenalty = false;

  public void evaluate(boolean yellow, boolean red) {
    if (!yellow && red)
      System.err.println("wrong use of PenaltyCards API");
    isPenalty = isPenalty ? yellow : red;
  }

  public boolean isPenalty() {
    return isPenalty;
  }
}
