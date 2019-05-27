// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapterTest;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.io.CsvFormat;
import junit.framework.TestCase;

public class VelodynePosChannelTest extends TestCase {
  public void testSimple() throws IOException {
    GokartLogInterface gokartLogInterface = GokartLogAdapterTest.FULL;
    // ---
    OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(VelodynePosChannel.INSTANCE);
    OfflineLogPlayer.process(gokartLogInterface.file(), offlineTableSupplier);
    Tensor tensor = offlineTableSupplier.getTable().map(CsvFormat.strict());
    assertEquals(tensor.length(), 102);
    MatrixQ.require(tensor);
  }
}
