// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.gokart.core.AutoboxSocketModule;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.GokartTimestampModule;
import ch.ethz.idsc.gokart.dev.u3.LabjackU3Module;
import ch.ethz.idsc.gokart.lcm.LoggerModule;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931LcmServerModule;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class QuickStartGuiTest extends TestCase {
  static final Set<Class<? extends AbstractModule>> HARDWARE = new HashSet<>(Arrays.asList( //
      AutoboxSocketModule.class, // sensing and actuation
      Vmu931LcmServerModule.class, // vmu931 imu
      GokartTimestampModule.class, //
      LoggerModule.class, //
      LabjackU3Module.class, //
      LidarLocalizationModule.class));

  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    for (Class<? extends AbstractModule> cls : RunTabbedTaskGui.MODULES_DEV)
      if (!HARDWARE.contains(cls)) {
        ModuleAuto.INSTANCE.runOne(cls);
        ModuleAuto.INSTANCE.endOne(cls);
      }
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
  }
}
