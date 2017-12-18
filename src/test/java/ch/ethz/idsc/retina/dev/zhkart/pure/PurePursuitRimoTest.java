// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PurePursuitRimoTest extends TestCase {
  public void testSimple() {
    PurePursuitRimo ppr = new PurePursuitRimo();
    assertFalse(ppr.putEvent().isPresent());
    Optional<RimoPutEvent> optional = ppr.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
    assertFalse(optional.isPresent()); // because speed reading is missing
  }
}
