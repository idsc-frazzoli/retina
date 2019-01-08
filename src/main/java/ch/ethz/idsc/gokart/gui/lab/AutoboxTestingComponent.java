// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import ch.ethz.idsc.gokart.core.AutoboxSocket;
import ch.ethz.idsc.gokart.core.GetListener;
import ch.ethz.idsc.gokart.core.PutListener;
import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.data.DataEvent;

/* package */ abstract class AutoboxTestingComponent<GE extends DataEvent, PE extends DataEvent> //
    extends ToolbarsComponent //
    implements GetListener<GE>, PutListener<PE>, PutProvider<PE>, StartAndStoppable {
  // ---
  @Override // from PutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  abstract AutoboxSocket<GE, PE> getSocket();

  @Override // from StartAndStoppable
  public final void start() {
    getSocket().addGetListener(this);
    getSocket().addPutListener(this);
    getSocket().addPutProvider(this);
  }

  @Override // from StartAndStoppable
  public final void stop() {
    getSocket().removePutProvider(this);
    getSocket().removePutListener(this);
    getSocket().removeGetListener(this);
  }
}
