// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.Comparator;

public enum PutProviderComparator implements Comparator<PutProvider<?>> {
  INSTANCE;
  // ---
  @Override
  public int compare(PutProvider<?> lhs, PutProvider<?> rhs) {
    int cmp = lhs.getProviderRank().compareTo(rhs.getProviderRank());
    if (cmp != 0)
      return cmp;
    if (lhs == rhs)
      return 0;
    return Integer.compare(1, 0); // <- deterministic effect on sorting unknown
  }
}
