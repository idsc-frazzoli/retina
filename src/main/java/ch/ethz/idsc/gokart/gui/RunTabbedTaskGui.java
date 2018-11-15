// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import ch.ethz.idsc.demo.mg.slam.online.DavisSlamLidarModule;
import ch.ethz.idsc.demo.mg.slam.online.DavisSlamOdometryModule;
import ch.ethz.idsc.demo.mg.slam.online.DavisSlamVisualModule;
import ch.ethz.idsc.demo.mg.slam.online.SEyeSlamLidarModule;
import ch.ethz.idsc.demo.mg.slam.online.SEyeSlamOdometryModule;
import ch.ethz.idsc.demo.mg.slam.online.SEyeSlamVisualModule;
import ch.ethz.idsc.gokart.core.AutoboxSocketModule;
import ch.ethz.idsc.gokart.core.fuse.AutonomySafetyModule;
import ch.ethz.idsc.gokart.core.fuse.DavisImuTrackerModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotCoolingModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotSafetyModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotTakeoverModule;
import ch.ethz.idsc.gokart.core.fuse.MiscEmergencyWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SpeedLimitSafetyModule;
import ch.ethz.idsc.gokart.core.fuse.SteerBatteryWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SteerCalibrationWatchdog;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.core.joy.GenericXboxPadLcmServerModule;
import ch.ethz.idsc.gokart.core.joy.ImprovedNormalizedTorqueVectoringJoystickModule;
import ch.ethz.idsc.gokart.core.joy.ImprovedTorqueVectoringJoystickModule;
import ch.ethz.idsc.gokart.core.joy.JoystickGroupModule;
import ch.ethz.idsc.gokart.core.joy.JoystickResetModule;
import ch.ethz.idsc.gokart.core.joy.LookupTableRimoThrustJoystickModule;
import ch.ethz.idsc.gokart.core.joy.RimoThrustJoystickModule;
import ch.ethz.idsc.gokart.core.joy.SimpleTorqueVectoringJoystickModule;
import ch.ethz.idsc.gokart.core.joy.SysidSignalsModule;
import ch.ethz.idsc.gokart.core.mpc.MPCKinematicDrivingModule;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmModule;
import ch.ethz.idsc.gokart.core.pure.FigureDucttapeModule;
import ch.ethz.idsc.gokart.core.pure.FigureEightModule;
import ch.ethz.idsc.gokart.core.pure.FigureEightReverseModule;
import ch.ethz.idsc.gokart.core.pure.FigureOvalModule;
import ch.ethz.idsc.gokart.core.pure.GokartTrajectoryModule;
import ch.ethz.idsc.gokart.core.pure.GokartTrajectorySRModule;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.SeesLcmModule;
import ch.ethz.idsc.gokart.gui.lab.AutoboxCompactModule;
import ch.ethz.idsc.gokart.gui.lab.AutoboxTestingModule;
import ch.ethz.idsc.gokart.gui.lab.LinmotPressTestModule;
import ch.ethz.idsc.gokart.gui.lab.LinmotSuccessivePressTestModule;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.gokart.gui.top.LocalViewLcmModule;
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
      DavisImuTrackerModule.class, //
      AutonomySafetyModule.class //
  );
  static final List<Class<?>> MODULES_CFG = Arrays.asList( //
      AutoboxIntrospectionModule.class, // actuation monitoring
      GlobalViewLcmModule.class, // initialize localization
      AutoboxCompactModule.class, // initialize actuation
      LocalViewLcmModule.class, //
      ParametersModule.class, // configure parameters
      SeesLcmModule.class //
  );
  static final List<Class<?>> MODULES_JOY = Arrays.asList( //
      RimoThrustJoystickModule.class, //
      SimpleTorqueVectoringJoystickModule.class, //
      ImprovedTorqueVectoringJoystickModule.class, //
      ImprovedNormalizedTorqueVectoringJoystickModule.class, //
      LookupTableRimoThrustJoystickModule.class, //
      JoystickGroupModule.class, //
      SysidSignalsModule.class //
  );
  static final List<Class<?>> MODULES_AUT = Arrays.asList( //
      FigureEightModule.class, //
      FigureEightReverseModule.class, //
      FigureOvalModule.class, //
      GokartTrajectoryModule.class, //
      GokartTrajectorySRModule.class, //
      DavisSlamLidarModule.class, //
      DavisSlamVisualModule.class, //
      DavisSlamOdometryModule.class, //
      SEyeSlamLidarModule.class, //
      SEyeSlamOdometryModule.class, //
      SEyeSlamVisualModule.class, //
      FigureDucttapeModule.class, //
      GokartTrajectoryModule.class, //
      GokartTrajectorySRModule.class, //
      MPCKinematicDrivingModule.class);
  static final List<Class<?>> MODULES_FUSE = Arrays.asList( //
      SpeedLimitSafetyModule.class, //
      SteerBatteryWatchdog.class, //
      LinmotCoolingModule.class, // TODO possibly auto start
      LinmotTakeoverModule.class //
  );
  static final List<Class<?>> MODULES_LAB = Arrays.asList( //
      SpyModule.class, //
      AutoboxTestingModule.class, //
      LinmotPressTestModule.class, //
      LinmotSuccessivePressTestModule.class, //
      // LocalViewLcmModule.class, //
      DavisDetailModule.class, //
      SeyeDetailModule.class, //
      PanoramaViewModule.class, // , //
      SideLcmModule.class, //
      PresenterLcmModule.class
  // DavisOverviewModule.class //
  );

  public static void main(String[] args) {
    WindowConfiguration windowConfiguration = //
        AppCustomization.load(RunTabbedTaskGui.class, new WindowConfiguration());
    TabbedTaskGui tabbedTaskGui = new TabbedTaskGui(PROPERTIES);
    tabbedTaskGui.tab("dev", MODULES_DEV);
    tabbedTaskGui.tab("cfg", MODULES_CFG);
    tabbedTaskGui.tab("joy", MODULES_JOY);
    tabbedTaskGui.tab("aut", MODULES_AUT);
    tabbedTaskGui.tab("fuse", MODULES_FUSE);
    tabbedTaskGui.tab("lab", MODULES_LAB);
    windowConfiguration.attach(RunTabbedTaskGui.class, tabbedTaskGui.jFrame);
    tabbedTaskGui.jFrame.setVisible(true);
  }
}
