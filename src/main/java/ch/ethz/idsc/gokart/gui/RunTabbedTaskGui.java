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
import ch.ethz.idsc.gokart.core.fuse.DavisImuTrackerModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotCoolingModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotSafetyModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotTakeoverModule;
import ch.ethz.idsc.gokart.core.fuse.MiscEmergencyWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SpeedLimitSafetyModule;
import ch.ethz.idsc.gokart.core.fuse.SteerBatteryWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SteerCalibrationWatchdog;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.core.man.ImprovedNormalizedPredictiveTorqueVectoringManualModule;
import ch.ethz.idsc.gokart.core.man.ImprovedNormalizedTorqueVectoringManualModule;
import ch.ethz.idsc.gokart.core.man.LookupTableRimoThrustManualModule;
import ch.ethz.idsc.gokart.core.man.ManualGroupModule;
import ch.ethz.idsc.gokart.core.man.ManualResetModule;
import ch.ethz.idsc.gokart.core.man.RimoThrustManualModule;
import ch.ethz.idsc.gokart.core.man.SysidSignalsModule;
import ch.ethz.idsc.gokart.core.map.GokartTrackReconModule;
import ch.ethz.idsc.gokart.core.mpc.MPCKinematicDrivingModule;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmModule;
import ch.ethz.idsc.gokart.core.pure.CenterLinePursuitModule;
import ch.ethz.idsc.gokart.core.pure.FigureDucttapeModule;
import ch.ethz.idsc.gokart.core.pure.FigureEightModule;
import ch.ethz.idsc.gokart.core.pure.FigureEightReverseModule;
import ch.ethz.idsc.gokart.core.pure.FigureOvalModule;
import ch.ethz.idsc.gokart.core.pure.FigureTiresAModule;
import ch.ethz.idsc.gokart.core.pure.FigureTiresBModule;
import ch.ethz.idsc.gokart.core.pure.GokartTrajectoryModule;
import ch.ethz.idsc.gokart.core.pure.GokartTrajectorySRModule;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.core.sound.GokartSoundLcmModule;
import ch.ethz.idsc.gokart.core.sound.GokartVoiceOutputs;
import ch.ethz.idsc.gokart.dev.GokartTimestampModule;
import ch.ethz.idsc.gokart.dev.SeesLcmModule;
import ch.ethz.idsc.gokart.dev.u3.LabjackU3LcmModule;
import ch.ethz.idsc.gokart.gui.lab.AutoboxCompactModule;
import ch.ethz.idsc.gokart.gui.lab.AutoboxTestingModule;
import ch.ethz.idsc.gokart.gui.lab.LinmotConstantPressTestModule;
import ch.ethz.idsc.gokart.gui.lab.LinmotPressTestModule;
import ch.ethz.idsc.gokart.gui.lab.TrackReconPanelModule;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.gokart.gui.top.LocalViewLcmModule;
import ch.ethz.idsc.gokart.gui.top.PresenterLcmModule;
import ch.ethz.idsc.gokart.gui.top.SideLcmModule;
import ch.ethz.idsc.gokart.lcm.LoggerModule;
import ch.ethz.idsc.gokart.lcm.SpyModule;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931LcmServerModule;
import ch.ethz.idsc.gokart.lcm.mod.AutoboxLcmServerModule;
import ch.ethz.idsc.gokart.lcm.mod.Vlp16LcmServerModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.TabbedTaskGui;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
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
      Vmu931LcmServerModule.class, // vmu931 imu
      AutoboxLcmServerModule.class, //
      GokartStatusLcmModule.class, //
      GokartPoseLcmModule.class, // publishes pose
      GokartTimestampModule.class, //
      LoggerModule.class, //
      LabjackU3LcmModule.class, //
      SteerCalibrationWatchdog.class, // <- DON'T REMOVE
      MiscEmergencyWatchdog.class, // <- DON'T REMOVE
      Vlp16PassiveSlowing.class, //
      LidarLocalizationModule.class, //
      LinmotSafetyModule.class, //
      ManualResetModule.class, //
      DavisImuTrackerModule.class, //
      GokartTrackReconModule.class //
  // AutonomySafetyModule.class //
  );
  static final List<Class<?>> MODULES_CFG = Arrays.asList( //
      AutoboxIntrospectionModule.class, // actuation monitoring
      GlobalViewLcmModule.class, // initialize localization
      AutoboxCompactModule.class, // initialize actuation
      LocalViewLcmModule.class, //
      ParametersModule.class, // configure parameters
      SeesLcmModule.class, //
      GokartSoundLcmModule.class, //
      GokartVoiceOutputs.class //
  );
  static final List<Class<?>> MODULES_MAN = Arrays.asList( //
      RimoThrustManualModule.class, //
      ImprovedNormalizedTorqueVectoringManualModule.class, //
      ImprovedNormalizedPredictiveTorqueVectoringManualModule.class, //
      LookupTableRimoThrustManualModule.class, //
      ManualGroupModule.class, //
      SysidSignalsModule.class //
  );
  static final List<Class<?>> MODULES_AUT = Arrays.asList( //
      TrackReconPanelModule.class, //
      MPCKinematicDrivingModule.class, //
      GokartTrajectoryModule.class, //
      CenterLinePursuitModule.class, //
      FigureTiresAModule.class, //
      FigureTiresBModule.class, //
      FigureEightModule.class, //
      FigureEightReverseModule.class, //
      FigureOvalModule.class, //
      GokartTrajectorySRModule.class, //
      DavisSlamLidarModule.class, //
      DavisSlamVisualModule.class, //
      DavisSlamOdometryModule.class, //
      SEyeSlamLidarModule.class, //
      SEyeSlamOdometryModule.class, //
      SEyeSlamVisualModule.class, //
      FigureDucttapeModule.class //
  );
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
      LinmotConstantPressTestModule.class, //
      // LocalViewLcmModule.class, //
      DavisDetailModule.class, //
      SeyeDetailModule.class, //
      PanoramaViewModule.class, // , //
      SideLcmModule.class, //
      PresenterLcmModule.class //
  // DavisOverviewModule.class //
  );

  public static void main(String[] args) {
    WindowConfiguration windowConfiguration = //
        AppCustomization.load(RunTabbedTaskGui.class, new WindowConfiguration());
    TabbedTaskGui tabbedTaskGui = new TabbedTaskGui(PROPERTIES);
    tabbedTaskGui.tab("dev", MODULES_DEV);
    tabbedTaskGui.tab("cfg", MODULES_CFG);
    tabbedTaskGui.tab("man", MODULES_MAN);
    tabbedTaskGui.tab("aut", MODULES_AUT);
    tabbedTaskGui.tab("fuse", MODULES_FUSE);
    tabbedTaskGui.tab("lab", MODULES_LAB);
    windowConfiguration.attach(RunTabbedTaskGui.class, tabbedTaskGui.jFrame);
    tabbedTaskGui.jFrame.setVisible(true);
  }
}
