// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.math.region.Polygons;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Mean;

public class ClusterDeque {
  private final Deque<DequeCloud> deque = new ArrayDeque<>();
  private final Deque<Tensor> means = new ArrayDeque<>();
  private final int id;

  public ClusterDeque(int id, Tensor value) {
    deque.add(new DequeCloud(value));
    this.id = id;
  }

  public Stream<Tensor> vertexStream() {
    return deque.stream().map(DequeCloud::points).flatMap(Tensor::stream);
  }

  public void removeFirst() { // TODO change it to parameter
    while (deque.size() > 4)
      deque.removeFirst();
  }

  public void appendEmpty() {
    deque.add(new DequeCloud(Tensors.empty()));
    means.add(Tensors.unmodifiableEmpty());
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
    deque.add(new DequeCloud(points));
    means.add(Tensors.isEmpty(points) ? Tensors.unmodifiableEmpty() : Mean.of(points));
  }

  public boolean isInside(Tensor point) {
    Tensor hull = deque.getLast().hull();
    return Polygons.isInside(hull, point);
  }

  public Collection<DequeCloud> getDeque() {
    return Collections.unmodifiableCollection(deque);
  }

  public DequeCloud getLast() {
    return deque.peekLast();
  }

  public int getID() {
    return id;
  }
}
