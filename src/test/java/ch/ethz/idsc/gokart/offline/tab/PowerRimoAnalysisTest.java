// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.io.IOException;
import java.util.Arrays;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapterTest;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PowerRimoAnalysisTest extends TestCase {
  public void testSimple() throws IOException {
    GokartLogInterface gokartLogInterface = GokartLogAdapterTest.FULL;
    // ---
    OfflineTableSupplier offlineTableSupplier = new PowerRimoAnalysis(Quantity.of(0, "s"));
    OfflineLogPlayer.process(gokartLogInterface.file(), offlineTableSupplier);
    Tensor tensor = offlineTableSupplier.getTable().map(CsvFormat.strict());
    // System.out.println(Dimensions.of(tensor));
    assertEquals(Dimensions.of(tensor), Arrays.asList(6005, 17));
  }
}
