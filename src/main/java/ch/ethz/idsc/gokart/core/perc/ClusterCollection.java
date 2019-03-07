// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Tensor;

public class ClusterCollection {
  private List<ClusterDeque> collection = new ArrayList<>();
  private int i = 0;

  public Tensor toMatrices() {
    return Tensor.of(collection.stream().map(ClusterDeque::vertexStream).map(Tensor::of));
  }

  public void maintainUntil(int size) {
    collection.subList(0, size).forEach(ClusterDeque::removeFirst);
    collection = collection.stream().filter(ClusterDeque::nonEmpty).collect(Collectors.toList());
  }

  public void addToCollection(Tensor value) {
    collection.add(new ClusterDeque(i, value));
    ++i;
  }

  public List<ClusterDeque> getCollection() {
    return Collections.unmodifiableList(collection);
  }

  public void decompose() {
    List<ClusterDeque> newDeques = new ArrayList<>();
    for (Iterator<ClusterDeque> iterator = collection.iterator(); iterator.hasNext();) {
      ClusterDeque x = iterator.next();
      Tensor vertices = Tensor.of(x.vertexStream());
      Tensor elkiDBSCAN = Dbscan.of(vertices, 0.03, 6);
      switch (elkiDBSCAN.length()) {
      case 0:
        // System.out.println("cluster is all noise");
        break;
      case 1:
        // original cluster
        break;
      default:
        iterator.remove();
        for (Tensor value : elkiDBSCAN) {
          newDeques.add(new ClusterDeque(i, value));
          ++i;
        }
        System.out.println("split to " + elkiDBSCAN.length());
      }
    }
    collection.addAll(newDeques);
  }

  public void removeDeques(Set<Integer> removeIndex) {
    int i = 0;
    for (Iterator<ClusterDeque> iterator = collection.iterator(); iterator.hasNext();) {
      iterator.next();
      if (removeIndex.contains(i)) {
        iterator.remove();
      }
      ++i;
    }
  }
}
