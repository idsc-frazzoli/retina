// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import ch.ethz.idsc.gokart.core.AutoboxSocketModule;
import ch.ethz.idsc.gokart.core.fuse.DavisImuWatchdog;
import ch.ethz.idsc.gokart.core.fuse.LinmotCoolingModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotEmergencyModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotTakeoverModule;
import ch.ethz.idsc.gokart.core.fuse.MiscEmergencyModule;
import ch.ethz.idsc.gokart.core.fuse.SteerEmergencyModule;
import ch.ethz.idsc.gokart.core.fuse.Vlp16ActiveSlowingModule;
import ch.ethz.idsc.gokart.core.fuse.Vlp16ClearanceModule;
import ch.ethz.idsc.gokart.core.joy.DeadManSwitchModule;
import ch.ethz.idsc.gokart.core.joy.JoystickGroupModule;
import ch.ethz.idsc.gokart.core.joy.SysIdSignalsModule;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmModule;
import ch.ethz.idsc.gokart.core.pure.PurePursuitModule;
import ch.ethz.idsc.gokart.gui.lab.AutoboxCompactModule;
import ch.ethz.idsc.gokart.gui.lab.AutoboxTestingModule;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.gokart.gui.top.SideLcmModule;
import ch.ethz.idsc.gokart.lcm.mod.AutoboxLcmServerModule;
import ch.ethz.idsc.gokart.lcm.mod.Vlp16LcmServerModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.sys.LoggerModule;
import ch.ethz.idsc.retina.sys.SpyModule;
import ch.ethz.idsc.retina.sys.TabbedTaskGui;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;
import ch.ethz.idsc.tensor.io.ResourceData;

/** RunTabbedTaskGui is a program that is typically for offline processing.
 * The window in a convenient way to launch files. */
enum RunTabbedTaskGui {
  ;
  /** file contains plain text brief description of modules */
  static final Properties PROPERTIES = //
      ResourceData.properties("/gui/properties/modules_description.properties");
  // ---
  static final List<Class<?>> MODULES_DEV = Arrays.asList( //
      AutoboxSocketModule.class, // sensing and actuation
      Vlp16LcmServerModule.class, // sensing
      AutoboxLcmServerModule.class, //
      GokartStatusLcmModule.class, //
      GokartPoseLcmModule.class, // move to DEV list
      LoggerModule.class //
  );
  static final List<Class<?>> MODULES_LAB = Arrays.asList( //
      SpyModule.class, //
      ParametersModule.class, //
      AutoboxIntrospectionModule.class, //
      AutoboxCompactModule.class, //
      AutoboxTestingModule.class, //
      // LocalViewLcmModule.class, //
      GlobalViewLcmModule.class, //
      DavisDetailModule.class, //
      PanoramaViewModule.class, // , //
      SideLcmModule.class //
  // DavisOverviewModule.class //
  );
  static final List<Class<?>> MODULES_FUSE = Arrays.asList( //
      MiscEmergencyModule.class, //
      SteerEmergencyModule.class, //
      LinmotCoolingModule.class, //
      LinmotTakeoverModule.class, //
      Vlp16ClearanceModule.class, //
      Vlp16ActiveSlowingModule.class, //
      DavisImuWatchdog.class //
  //
  );
  static final List<Class<?>> MODULES_JOY = Arrays.asList( //
      LinmotEmergencyModule.class, //
      DeadManSwitchModule.class, // joystick
      JoystickGroupModule.class, //
      SysIdSignalsModule.class //
  );
  static final List<Class<?>> MODULES_AUT = Arrays.asList( //
      PurePursuitModule.class //
  );

  public static void main(String[] args) {
    WindowConfiguration wc = AppCustomization.load(RunTabbedTaskGui.class, new WindowConfiguration());
    TabbedTaskGui taskTabGui = new TabbedTaskGui(PROPERTIES);
    taskTabGui.tab("dev", MODULES_DEV);
    taskTabGui.tab("lab", MODULES_LAB);
    taskTabGui.tab("fuse", MODULES_FUSE);
    taskTabGui.tab("joy", MODULES_JOY);
    taskTabGui.tab("aut", MODULES_AUT);
    wc.attach(RunTabbedTaskGui.class, taskTabGui.jFrame);
    taskTabGui.jFrame.setVisible(true);
  }
}
