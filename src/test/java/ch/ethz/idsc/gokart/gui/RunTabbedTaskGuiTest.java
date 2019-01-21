// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.core.AutoboxSocketModule;
import ch.ethz.idsc.gokart.core.fuse.DavisImuTrackerModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotSafetyModule;
import ch.ethz.idsc.gokart.core.fuse.MiscEmergencyWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SteerCalibrationWatchdog;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.core.man.ManualResetModule;
import ch.ethz.idsc.gokart.core.man.SysidSignalsModule;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmModule;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.u3.LabjackU3LcmModule;
import ch.ethz.idsc.gokart.lcm.LoggerModule;
import ch.ethz.idsc.gokart.lcm.mod.AutoboxLcmServerModule;
import ch.ethz.idsc.gokart.lcm.mod.Vlp16LcmServerModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class RunTabbedTaskGuiTest extends TestCase {
  public void testSimple() {
    assertTrue(RunTabbedTaskGui.MODULES_DEV.contains(SteerCalibrationWatchdog.class));
    assertTrue(RunTabbedTaskGui.MODULES_DEV.contains(MiscEmergencyWatchdog.class));
  }

  public void testAutonomous() throws InterruptedException {
    for (Class<?> cls : RunTabbedTaskGui.MODULES_AUT) {
      ModuleAuto.INSTANCE.runOne(cls);
      Thread.sleep(150); // needs time to start thread that invokes first()
      ModuleAuto.INSTANCE.endOne(cls);
    }
  }

  public void testJoystick() throws InterruptedException {
    for (Class<?> cls : RunTabbedTaskGui.MODULES_MAN)
      if (!cls.equals(SysidSignalsModule.class)) {
        ModuleAuto.INSTANCE.runOne(cls);
        Thread.sleep(50); // needs time to start thread that invokes first()
        ModuleAuto.INSTANCE.endOne(cls);
      } else
        System.out.println("skip " + cls);
  }

  public void testAutonomousSafety() {
    List<Class<?>> list = Arrays.asList( //
        AutoboxSocketModule.class, //
        Vlp16LcmServerModule.class, //
        AutoboxLcmServerModule.class, //
        GokartStatusLcmModule.class, //
        GokartPoseLcmModule.class, //
        LoggerModule.class, //
        // GenericXboxPadLcmServerModule.class, //
        LabjackU3LcmModule.class, //
        SteerCalibrationWatchdog.class, //
        MiscEmergencyWatchdog.class, //
        Vlp16PassiveSlowing.class, //
        LidarLocalizationModule.class, //
        LinmotSafetyModule.class, //
        ManualResetModule.class, //
        DavisImuTrackerModule.class// , //
    // AutonomySafetyModule.class //
    );
    for (Class<?> cls : list)
      assertTrue(RunTabbedTaskGui.MODULES_DEV.contains(cls));
  }
}
