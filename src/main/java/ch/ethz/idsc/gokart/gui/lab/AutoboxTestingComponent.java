// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import ch.ethz.idsc.gokart.core.GetListener;
import ch.ethz.idsc.gokart.core.ProviderRank;
import ch.ethz.idsc.gokart.core.PutListener;
import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.gui.ToolbarsComponent;

/* package */ abstract class AutoboxTestingComponent<GE, PE> extends ToolbarsComponent //
    implements GetListener<GE>, PutListener<PE>, PutProvider<PE> {
  // ---
  @Override // from PutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }
}
