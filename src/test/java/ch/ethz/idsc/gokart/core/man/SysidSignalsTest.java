// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class SysidSignalsTest extends TestCase {
  public void testSimple() {
    for (SysidSignals sysidSignals : SysidSignals.values()) {
      ScalarUnaryOperator scalarUnaryOperator = sysidSignals.get();
      scalarUnaryOperator.apply(RealScalar.ZERO);
      scalarUnaryOperator.apply(RealScalar.ONE);
      scalarUnaryOperator.apply(RealScalar.of(0.123));
      scalarUnaryOperator.apply(RealScalar.of(4.567));
    }
  }
}
