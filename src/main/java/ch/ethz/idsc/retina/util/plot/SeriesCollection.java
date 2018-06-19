// code by jph
package ch.ethz.idsc.retina.util.plot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.red.Entrywise;

public class SeriesCollection implements Iterable<SeriesContainer> {
  private String title = "untitled";
  private final List<SeriesContainer> list = new ArrayList<>();

  /** @param xData vector
   * @param yData vector
   * @return */
  public SeriesContainer add(Tensor xData, Tensor yData) {
    return add(Transpose.of(Tensors.of(xData, yData)));
  }

  public SeriesContainer add(Tensor points) {
    Color color = ColorDataLists._097.cyclic().getColor(list.size());
    SeriesContainer seriesContainer = new SeriesContainer(points, color);
    list.add(seriesContainer);
    return seriesContainer;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  /** @return {{x_min, x_max}, {y_min, y_max}} */
  public Tensor getPlotRange() {
    return Transpose.of(Tensors.of( //
        list.stream().map(SeriesContainer::points).flatMap(Tensor::stream).reduce(Entrywise.min()).get(), //
        list.stream().map(SeriesContainer::points).flatMap(Tensor::stream).reduce(Entrywise.max()).get()));
  }

  public Stream<SeriesContainer> stream() {
    return list.stream();
  }

  @Override
  public Iterator<SeriesContainer> iterator() {
    return list.iterator();
  }
}
