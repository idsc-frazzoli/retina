// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SysidRimoModuleTest extends TestCase {
  public void testSimple() throws Exception {
    SysidRimoModule sysidRimoModule = new SysidRimoModule();
    sysidRimoModule.first();
    Optional<RimoPutEvent> optional = sysidRimoModule.putEvent();
    assertFalse(optional.isPresent());
    sysidRimoModule.last();
  }

  public void testValue() throws Exception {
    SysidRimoModule sysidRimoModule = new SysidRimoModule();
    {
      RimoPutEvent rpe = sysidRimoModule.create(RealScalar.of(.6), DoubleScalar.of(1.5));
      assertEquals(rpe.getTorque_Y_pair(), Tensors.fromString("{900[ARMS], 900[ARMS]}"));
    }
    {
      RimoPutEvent rpe = sysidRimoModule.create(RealScalar.of(.6), DoubleScalar.of(2.5));
      assertEquals(rpe.getTorque_Y_pair(), Tensors.fromString("{-900[ARMS], -900[ARMS]}"));
    }
  }
}
