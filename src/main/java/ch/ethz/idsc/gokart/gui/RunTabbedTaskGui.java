// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import ch.ethz.idsc.demo.mg.slam.online.DavisSlamLidarModule;
import ch.ethz.idsc.demo.mg.slam.online.DavisSlamOdometryModule;
import ch.ethz.idsc.demo.mg.slam.online.DavisSlamVisualModule;
import ch.ethz.idsc.gokart.core.AutoboxSocketModule;
import ch.ethz.idsc.gokart.core.fuse.DavisImuTrackerModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotCoolingModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotSafetyModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotTakeoverModule;
import ch.ethz.idsc.gokart.core.fuse.MiscEmergencyWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SteerBatteryWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SteerCalibrationWatchdog;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.core.joy.GenericXboxPadLcmServerModule;
import ch.ethz.idsc.gokart.core.joy.JoystickGroupModule;
import ch.ethz.idsc.gokart.core.joy.JoystickResetModule;
import ch.ethz.idsc.gokart.core.joy.RimoThrustJoystickModule;
import ch.ethz.idsc.gokart.core.joy.SysidSignalsModule;
import ch.ethz.idsc.gokart.core.joy.TorqueVectoringJoystickModule;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmModule;
import ch.ethz.idsc.gokart.core.pure.FigureDucttapeModule;
import ch.ethz.idsc.gokart.core.pure.FigureEightModule;
import ch.ethz.idsc.gokart.core.pure.FigureEightReverseModule;
import ch.ethz.idsc.gokart.core.pure.FigureOvalModule;
import ch.ethz.idsc.gokart.core.pure.GokartTrajectoryModule;
import ch.ethz.idsc.gokart.core.pure.GokartTrajectorySRModule;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.gui.lab.AutoboxCompactModule;
import ch.ethz.idsc.gokart.gui.lab.AutoboxTestingModule;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.gokart.gui.top.PresenterLcmModule;
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
      GokartPoseLcmModule.class, // publishes pose
      LoggerModule.class, //
      GenericXboxPadLcmServerModule.class, //
      SteerCalibrationWatchdog.class, // <- DON'T REMOVE
      MiscEmergencyWatchdog.class, // <- DON'T REMOVE
      Vlp16PassiveSlowing.class, //
      LidarLocalizationModule.class, //
      LinmotSafetyModule.class, //
      JoystickResetModule.class, //
      DavisImuTrackerModule.class //
  );
  static final List<Class<?>> MODULES_CFG = Arrays.asList( //
      AutoboxIntrospectionModule.class, // actuation monitoring
      GlobalViewLcmModule.class, // initialize localization
      AutoboxCompactModule.class, // initialize actuation
      ParametersModule.class // configure parameters
  );
  static final List<Class<?>> MODULES_JOY = Arrays.asList( //
      RimoThrustJoystickModule.class, //
      TorqueVectoringJoystickModule.class, //
      JoystickGroupModule.class, //
      SysidSignalsModule.class //
  );
  static final List<Class<?>> MODULES_AUT = Arrays.asList( //
      FigureEightModule.class, //
      FigureEightReverseModule.class, //
      FigureOvalModule.class, //
      DavisSlamLidarModule.class, //
      DavisSlamVisualModule.class, //
      DavisSlamOdometryModule.class, //
      FigureDucttapeModule.class, //
      GokartTrajectoryModule.class, //
      GokartTrajectorySRModule.class //
  );
  static final List<Class<?>> MODULES_FUSE = Arrays.asList( //
      SteerBatteryWatchdog.class, //
      LinmotCoolingModule.class, // TODO possibly auto start
      LinmotTakeoverModule.class //
  // Vlp16ActiveSlowingModule.class, // no option until speed controller reliable
  //
  );
  static final List<Class<?>> MODULES_LAB = Arrays.asList( //
      SpyModule.class, //
      AutoboxTestingModule.class, //
      // LocalViewLcmModule.class, //
      DavisDetailModule.class, //
      PanoramaViewModule.class, // , //
      SideLcmModule.class, //
      PresenterLcmModule.class
  // DavisOverviewModule.class //
  );

  public static void main(String[] args) {
    WindowConfiguration wc = AppCustomization.load(RunTabbedTaskGui.class, new WindowConfiguration());
    TabbedTaskGui tabbedTaskGui = new TabbedTaskGui(PROPERTIES);
    tabbedTaskGui.tab("dev", MODULES_DEV);
    tabbedTaskGui.tab("cfg", MODULES_CFG);
    tabbedTaskGui.tab("joy", MODULES_JOY);
    tabbedTaskGui.tab("aut", MODULES_AUT);
    tabbedTaskGui.tab("fuse", MODULES_FUSE);
    tabbedTaskGui.tab("lab", MODULES_LAB);
    wc.attach(RunTabbedTaskGui.class, tabbedTaskGui.jFrame);
    tabbedTaskGui.jFrame.setVisible(true);
  }
}
