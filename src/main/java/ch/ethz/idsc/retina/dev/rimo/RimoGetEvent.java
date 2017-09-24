// code by jph
package ch.ethz.idsc.retina.dev.rimo;

public class RimoGetEvent {
  public static final int LENGTH = 2 * RimoGetTire.LENGTH;
  // ---
  public final RimoGetTire getL;
  public final RimoGetTire getR;

  public RimoGetEvent(RimoGetTire getL, RimoGetTire getR) {
    this.getL = getL;
    this.getR = getR;
  }
}
