// code by jph
package ch.ethz.idsc.retina.gui.gokart.lab;

import ch.ethz.idsc.retina.dev.zhkart.GetListener;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.dev.zhkart.PutListener;
import ch.ethz.idsc.retina.dev.zhkart.PutProvider;
import ch.ethz.idsc.retina.gui.gokart.ToolbarsComponent;

/* package */ abstract class AutoboxTestingComponent<GE, PE> extends ToolbarsComponent //
    implements GetListener<GE>, PutListener<PE>, PutProvider<PE> {
  @Override
  public final ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }
}
