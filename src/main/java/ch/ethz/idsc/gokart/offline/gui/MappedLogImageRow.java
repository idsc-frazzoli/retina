// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.util.Map;

import ch.ethz.idsc.tensor.Scalar;

/* package */ abstract class MappedLogImageRow extends GokartLogImageRow {
  public abstract Map<Scalar, String> legend();
}
