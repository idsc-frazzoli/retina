// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import junit.framework.TestCase;

public class SteerSocketTest extends TestCase {
  public void testSimple() {
    try {
      SteerSocket.INSTANCE.addProvider(SteerPutFallback.INSTANCE);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    SteerSocket.INSTANCE.removeProvider(SteerPutFallback.INSTANCE);
    SteerSocket.INSTANCE.addProvider(SteerPutFallback.INSTANCE);
    SteerPutProvider spp1 = new SteerPutProvider() {
      @Override
      public Optional<SteerPutEvent> getPutEvent() {
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
    SteerSocket.INSTANCE.addProvider(spp1);
    try {
      SteerSocket.INSTANCE.addProvider(spp1);
    } catch (Exception exception) {
      // ---
    }
    SteerPutProvider spp2 = new SteerPutProvider() {
      @Override
      public Optional<SteerPutEvent> getPutEvent() {
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
    SteerSocket.INSTANCE.addProvider(spp2);
    // System.out.println(SteerSocket.INSTANCE.providers);
  }
}
