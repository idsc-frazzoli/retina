package ch.ethz.idsc.gokart.core.perc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Tensor;

public class ClusterCollection {
  public List<ClusterDeque> collection = new ArrayList<>();

  public Tensor toMatrices() {
    return Tensor.of(collection.stream().map(c -> Tensor.of(c.vertexStream())));
  }

  public void maintainUntil(int size) {
    collection.subList(0, size).forEach(ClusterDeque::removeFirst);
    collection = collection.stream().filter(ClusterDeque::nonEmpty).collect(Collectors.toList());
  }
}
