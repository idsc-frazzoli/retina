// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.util.img.DimensionInterface;

public interface DavisDevice extends DimensionInterface {
  DavisDecoder createDecoder();
}
