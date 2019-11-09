// code by am, jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class PowerSteeringModuleTest extends TestCase {
  public void testSimple() {
    PowerSteeringModule powerSteeringModule = new NaivePowerSteeringModule();
    powerSteeringModule.first();
    assertFalse(powerSteeringModule.putEvent().isPresent());
    powerSteeringModule.last();
  }

  public void testWithLocalization() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    PowerSteeringModule powerSteeringModule = new NaivePowerSteeringModule();
    powerSteeringModule.first();
    assertFalse(powerSteeringModule.putEvent().isPresent());
    powerSteeringModule.last();
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
  }
}
