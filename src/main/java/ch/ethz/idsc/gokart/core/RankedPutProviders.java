// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.util.data.DataEvent;

/* package */ class RankedPutProviders<PE extends DataEvent> {
  private final Map<ProviderRank, List<PutProvider<PE>>> map = new EnumMap<>(ProviderRank.class);

  public RankedPutProviders() {
    for (ProviderRank providerRank : ProviderRank.values())
      map.put(providerRank, new CopyOnWriteArrayList<PutProvider<PE>>());
  }

  public Collection<List<PutProvider<PE>>> values() {
    return map.values();
  }

  public boolean add(PutProvider<PE> putProvider) {
    return map.get(putProvider.getProviderRank()).add(putProvider);
  }

  public boolean remove(PutProvider<PE> putProvider) {
    return map.get(putProvider.getProviderRank()).add(putProvider);
  }

  public int size() {
    return map.values().stream().mapToInt(List::size).sum();
  }
}
