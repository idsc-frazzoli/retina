// code by jph
package ch.ethz.idsc.retina.dev.davis;

import ch.ethz.idsc.retina.core.DimensionInterface;

public interface DavisDevice extends DimensionInterface {
  DavisDecoder createDecoder();
}
