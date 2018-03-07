// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Optional;

import ch.ethz.idsc.owl.math.state.ProviderRank;
import junit.framework.TestCase;

public class LinmotSocketTest extends TestCase {
  public void testSimple() {
    LinmotSocket.INSTANCE.getPutProviderDesc();
  }

  public void testPeriod() {
    assertEquals(LinmotSocket.INSTANCE.getPutPeriod_ms(), 20);
  }

  public void testGetPutProviderRank() {
    Optional<ProviderRank> optional = LinmotSocket.INSTANCE.getPutProviderRank();
    assertFalse(optional.isPresent());
  }

  public void testStop() {
    LinmotSocket.INSTANCE.stop();
  }
}
