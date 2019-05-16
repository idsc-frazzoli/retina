// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.util.function.Supplier;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.io.TableBuilder;

abstract class GokartLogImageRow implements Supplier<Scalar> {
  protected final TableBuilder tableBuilder = new TableBuilder();

  public final void append() {
    tableBuilder.appendRow(get());
  }

  public final Tensor tensor() {
    return tableBuilder.toTable();
  }

  public abstract ColorDataGradient getColorDataGradient();
}
