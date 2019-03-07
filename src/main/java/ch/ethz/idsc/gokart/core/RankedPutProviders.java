// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.data.DataEvent;

/** thread-safe data structure to maintain collection of put providers of various ranks */
/* package */ class RankedPutProviders<PE extends DataEvent> {
  private final Map<ProviderRank, List<PutProvider<PE>>> map = new EnumMap<>(ProviderRank.class);

  public RankedPutProviders() {
    for (ProviderRank providerRank : ProviderRank.values())
      map.put(providerRank, new CopyOnWriteArrayList<PutProvider<PE>>());
  }

  /** @return sorted collection of providers in order of rank */
  public Collection<List<PutProvider<PE>>> values() {
    return map.values();
  }

  /** @param putProvider
   * @return whether given putProvider was added to collection */
  public boolean add(PutProvider<PE> putProvider) {
    return map.get(putProvider.getProviderRank()).add(putProvider);
  }

  /** @param putProvider
   * @return whether given putProvider was removed to collection */
  public boolean remove(PutProvider<PE> putProvider) {
    return map.get(putProvider.getProviderRank()).remove(putProvider);
  }

  /** @return number of total providers stored in map */
  public int size() {
    return map.values().stream().mapToInt(List::size).sum();
  }
}
