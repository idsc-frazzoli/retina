// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.ProviderRank;
import junit.framework.TestCase;

public class SteerSocketTest extends TestCase {
  public void testRate() {
    assertEquals(SteerSocket.INSTANCE.getPutPeriod_ms(), 20);
  }

  public void testSimple() {
    SteerPutProvider spp1 = new SteerPutProvider() {
      @Override
      public Optional<SteerPutEvent> putEvent() {
        return null;
      }

      @Override
      public ProviderRank getProviderRank() {
        return ProviderRank.FALLBACK;
      }

      @Override
      public String toString() {
        return "add1";
      }
    };
    SteerSocket.INSTANCE.addPutProvider(spp1);
    SteerPutProvider spp2 = new SteerPutProvider() {
      @Override
      public Optional<SteerPutEvent> putEvent() {
        return null;
      }

      @Override
      public ProviderRank getProviderRank() {
        return ProviderRank.FALLBACK;
      }

      @Override
      public String toString() {
        return "add2";
      }
    };
    SteerSocket.INSTANCE.addPutProvider(spp2);
  }
}
