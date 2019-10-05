// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ abstract class GokartLogImageRow {
  protected final TableBuilder tableBuilder = new TableBuilder();

  public final void append() {
    tableBuilder.appendRow(getScalar());
  }

  public final Tensor tensor() {
    return tableBuilder.getTable();
  }

  /** @return value in the interval [0, 1] */
  public abstract Scalar getScalar();

  /** @return color data gradient */
  public abstract ColorDataGradient getColorDataGradient();

  /** @return name */
  public abstract String getName();
}
