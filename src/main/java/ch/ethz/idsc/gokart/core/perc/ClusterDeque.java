// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Mean;

public class ClusterDeque {
  private final Deque<Tensor> deque = new ArrayDeque<>();
  private final Deque<Tensor> means = new ArrayDeque<>();
  private final int id;

  public ClusterDeque(int id, Tensor value) {
    deque.add(value);
    this.id = id;
  }

  public Stream<Tensor> vertexStream() {
    return deque.stream().flatMap(Tensor::stream);
  }

  public void removeFirst() { // TODO change it to parameter
    while (deque.size() > 4)
      deque.removeFirst();
  }

  public void appendEmpty() {
    deque.add(Tensors.empty());
    means.add(Tensors.empty());
  }

  public boolean nonEmpty() {
    return vertexStream().findFirst().isPresent();
  }

  public Tensor getNonEmptyMeans() {
    return Tensor.of(means.stream().filter(Tensors::nonEmpty));
  }

  public void replaceLast(Tensor points) {
    deque.removeLast();
    means.removeLast();
    deque.add(points);
    means.add(Tensors.isEmpty(points) ? Tensors.empty() : Mean.of(points));
  }

  public Collection<Tensor> getDeque() {
    return Collections.unmodifiableCollection(deque);
  }

  public int getID() {
    return id;
  }
}
