// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.Comparator;

public enum PutProviderComparator implements Comparator<PutProvider<?>> {
  INSTANCE;
  // ---
  @Override
  public int compare(PutProvider<?> lhs, PutProvider<?> rhs) {
    return lhs.getProviderRank().compareTo(rhs.getProviderRank());
  }
}
