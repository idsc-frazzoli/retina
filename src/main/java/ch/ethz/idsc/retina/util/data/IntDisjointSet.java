// code from stackoverflow  
// adapted by jph
package ch.ethz.idsc.retina.util.data;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntDisjointSet {
  private final int[] parent;
  private final int[] rank;

  public IntDisjointSet(int size) {
    parent = IntStream.range(0, size).toArray();
    rank = new int[size];
  }

  /** @param index
   * @return representative of index */
  public int find(int index) {
    if (parent[index] != index)
      parent[index] = find(parent[index]); // path collapse
    return parent[index];
  }

  /** @param index1
   * @param index2 */
  public void union(int index1, int index2) {
    int x_root = find(index1);
    int y_root = find(index2);
    if (x_root != y_root) { // <- confirmed
      if (rank[x_root] < rank[y_root])
        parent[x_root] = y_root;
      else //
      if (rank[x_root] > rank[y_root])
        parent[y_root] = x_root;
      else {
        parent[y_root] = x_root;
        ++rank[x_root]; // <- confirmed
      }
    }
  }

  /***************************************************/
  // functions for testing
  /* package */ int depth(int index) {
    int depth = 0;
    while (parent[index] != index) {
      index = parent[index];
      ++depth;
    }
    return depth;
  }

  /* package */ int maxRank() {
    return IntStream.of(rank).reduce(Math::max).getAsInt();
  }

  /* package */ Collection<Integer> parents() {
    return IntStream.of(parent).boxed().collect(Collectors.toSet());
  }

  /* package */ Collection<Integer> representatives() {
    return IntStream.range(0, parent.length) //
        .map(this::find) //
        .boxed().collect(Collectors.toSet());
  }
}
