// code by jph
package ch.ethz.idsc.retina.u3;

@FunctionalInterface
public interface LabjackAdcListener {
  /** @param labjackAdcFrame */
  void labjackAdc(LabjackAdcFrame labjackAdcFrame);
}
