// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import ch.ethz.idsc.gokart.core.GetListener;
import ch.ethz.idsc.gokart.core.PutListener;
import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.owl.math.state.ProviderRank;

/* package */ abstract class AutoboxTestingComponent<GE, PE> extends ToolbarsComponent //
    implements GetListener<GE>, PutListener<PE>, PutProvider<PE> {
  // ---
  @Override // from PutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }
}
