// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import junit.framework.TestCase;

public class LinmotComponentTest extends TestCase {
  public void testSimple() {
    LinmotComponent linmotComponent = new LinmotComponent();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
    linmotComponent.getEvent(linmotGetEvent);
    Optional<LinmotPutEvent> optional = linmotComponent.putEvent();
    assertTrue(optional.isPresent());
    LinmotPutEvent linmotPutEvent = optional.get();
    linmotComponent.putEvent(linmotPutEvent);
    assertTrue(Objects.nonNull(linmotComponent.getScrollPane()));
  }

  public void testRank() {
    LinmotComponent linmotComponent = new LinmotComponent();
    assertEquals(linmotComponent.getProviderRank(), ProviderRank.TESTING);
  }

  public void testMinPoints() {
    int min_points = LocalizationConfig.GLOBAL.min_points.number().intValue();
    assertTrue(150 < min_points);
    assertTrue(min_points < 300);
  }
}
