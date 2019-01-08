// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.Comparator;

import ch.ethz.idsc.owl.ani.api.ProviderRank;

/** comparator for collections of {@link PutProvider}s according to their
 * {@link ProviderRank}.
 * 
 * When the comparator is used to order a set, the comparator ideally
 * ensures that not two identical instances are the in the set. but this
 * is not the case. in particular, that means that removal of an element
 * requires to iterate over all elements in the set. */
public enum PutProviderComparator implements Comparator<PutProvider<?>> {
  INSTANCE;
  // ---
  private static final int GREATER = Integer.compare(1, 0);

  @Override // from Comparator
  public int compare(PutProvider<?> lhs, PutProvider<?> rhs) {
    int cmp = lhs.getProviderRank().compareTo(rhs.getProviderRank());
    if (cmp != 0)
      return cmp;
    if (lhs == rhs) // comparison of exact references
      return 0;
    return GREATER; // <- deterministic effect on sorting unknown
  }
}
