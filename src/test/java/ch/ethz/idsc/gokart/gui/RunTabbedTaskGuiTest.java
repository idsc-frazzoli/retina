// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.core.AutoboxSocketModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotSafetyModule;
import ch.ethz.idsc.gokart.core.fuse.MiscEmergencyWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SteerCalibrationWatchdog;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.core.man.ManualResetModule;
import ch.ethz.idsc.gokart.core.man.SysidSignalsModule;
import ch.ethz.idsc.gokart.core.mpc.MPCAbstractDrivingModule;
import ch.ethz.idsc.gokart.core.pos.PoseLcmServerModule;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.u3.LabjackU3Module;
import ch.ethz.idsc.gokart.lcm.LoggerModule;
import ch.ethz.idsc.gokart.lcm.mod.AutoboxLcmServerModule;
import ch.ethz.idsc.gokart.lcm.mod.Vlp16RayLcmServerModule;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class RunTabbedTaskGuiTest extends TestCase {
  public void testSimple() {
    assertTrue(RunTabbedTaskGui.MODULES_DEV.contains(SteerCalibrationWatchdog.class));
    assertTrue(RunTabbedTaskGui.MODULES_DEV.contains(MiscEmergencyWatchdog.class));
  }

  public void testAutonomous() throws Exception {
    for (Class<? extends AbstractModule> module : RunTabbedTaskGui.MODULES_AUT)
      // skip MPC related modules in tests
      if (!MPCAbstractDrivingModule.class.isAssignableFrom(module)) {
        ModuleAuto.INSTANCE.runOne(module);
        Thread.sleep(150); // needs time to start thread that invokes first()
        ModuleAuto.INSTANCE.endOne(module);
      }
  }

  public void testJoystick() throws Exception {
    for (Class<? extends AbstractModule> cls : RunTabbedTaskGui.MODULES_MAN)
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
        Vlp16RayLcmServerModule.class, //
        AutoboxLcmServerModule.class, //
        GokartStatusLcmModule.class, //
        PoseLcmServerModule.class, //
        LoggerModule.class, //
        // GenericXboxPadLcmServerModule.class, //
        LabjackU3Module.class, //
        SteerCalibrationWatchdog.class, //
        MiscEmergencyWatchdog.class, //
        Vlp16PassiveSlowing.class, //
        LinmotSafetyModule.class, //
        LidarLocalizationModule.class, //
        // AutonomousEmergencyModule.class, //
        ManualResetModule.class //
    // AutonomySafetyModule.class //
    );
    for (Class<?> cls : list)
      assertTrue(RunTabbedTaskGui.MODULES_DEV.contains(cls));
  }
}
