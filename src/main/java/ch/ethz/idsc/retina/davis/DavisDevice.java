// code by jph
package ch.ethz.idsc.retina.davis;

public interface DavisDevice extends DimensionInterface {
  DavisDecoder createDecoder();
}
