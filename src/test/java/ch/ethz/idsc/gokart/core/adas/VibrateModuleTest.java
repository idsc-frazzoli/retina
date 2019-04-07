// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.ManualControlAdapter;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class VibrateModuleTest extends TestCase {
  public void testSimple() {
    VibrateModule vibrateModule = new VibrateModule();
    vibrateModule.first();
    assertFalse(vibrateModule.putEvent().isPresent());
    vibrateModule.last();
  }

  public void testSimple1() {
    VibrateModule vibrateModule = new VibrateModule();
    vibrateModule.first();
    assertFalse(vibrateModule.steerEvent(Optional.empty()).isPresent());
    vibrateModule.last();
  }

  public void testSimple2() {
    VibrateModule vibrateModule = new VibrateModule();
    vibrateModule.first();
    ManualControlAdapter manualControlAdapter = new ManualControlAdapter(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, Tensors.vector(0, 0), true, false);
    assertTrue(vibrateModule.steerEvent(Optional.of(manualControlAdapter)).isPresent());
    vibrateModule.last();
  }
}
