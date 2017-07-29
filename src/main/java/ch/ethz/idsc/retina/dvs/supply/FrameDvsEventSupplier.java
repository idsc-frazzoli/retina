// code by jph
package ch.ethz.idsc.retina.dvs.supply;

public interface FrameDvsEventSupplier extends DvsEventSupplier {
  void handle(TimedFrame timedFrame);

  boolean isEmpty();
}
