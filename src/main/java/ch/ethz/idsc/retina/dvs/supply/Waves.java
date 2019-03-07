// code by jph
package ch.ethz.idsc.retina.dvs.supply;

import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public enum Waves {
  ;
  public static DvsEventSupplier create(Dimension dimension) {
    Collection<DvsEvent> collection = new LinkedList<>();
    for (int x = 0; x < dimension.width; ++x)
      for (int y = 0; y < dimension.height; ++y) {
        Scalar norm = Norm._2.of(Tensors.vector(x * 5000, y * 10000));
        int i = 0;
        for (int t = 0; t < 10; ++t) {
          long time_us = norm.number().longValue() + t * 300_000;
          DvsEvent dvsEvent = new DvsEvent(time_us, x, y, i % 2);
          collection.add(dvsEvent);
          ++i;
        }
      }
    return new QueuedDvsEventSupplier(collection, dimension);
  }
}
