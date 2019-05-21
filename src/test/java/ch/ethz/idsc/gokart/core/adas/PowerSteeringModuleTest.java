// code by am, jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvents;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PowerSteeringModuleTest extends TestCase {
  public void testSimple() {
    PowerSteeringModule powerSteeringModule = new PowerSteeringModule();
    powerSteeringModule.first();
    assertFalse(powerSteeringModule.putEvent().isPresent());
    powerSteeringModule.last();
  }

  public void testWithLocalization() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    PowerSteeringModule powerSteeringModule = new PowerSteeringModule();
    powerSteeringModule.first();
    assertFalse(powerSteeringModule.putEvent().isPresent());
    powerSteeringModule.last();
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
  }

  public void testNonNull() {
    PowerSteeringModule powerSteeringModule = new PowerSteeringModule();
    powerSteeringModule.first();
    powerSteeringModule.getEvent(SteerGetEvents.ZEROS);
    Scalar scalar = powerSteeringModule.putEvent(Quantity.of(0.2, "SCE"), Tensors.of( //
        Quantity.of(2, SI.VELOCITY), //
        Quantity.of(0.3, SI.VELOCITY), //
        Quantity.of(1, SI.PER_SECOND)), //
        Quantity.of(0.3, "SCT"));
    assertTrue(Scalars.nonZero(SteerPutEvent.RTORQUE.apply(scalar)));
    powerSteeringModule.last();
  }
}
