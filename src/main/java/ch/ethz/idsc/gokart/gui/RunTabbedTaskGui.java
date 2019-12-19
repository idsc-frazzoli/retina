// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import ch.ethz.idsc.gokart.core.AutoboxSocketModule;
import ch.ethz.idsc.gokart.core.adas.AntilockBrakeModule;
import ch.ethz.idsc.gokart.core.adas.ConstantTorqueSteerModule;
import ch.ethz.idsc.gokart.core.adas.LaneKeepingLimitedSteeringModule;
import ch.ethz.idsc.gokart.core.adas.NaivePowerSteeringModule;
import ch.ethz.idsc.gokart.core.adas.NoFrictionExperiment;
import ch.ethz.idsc.gokart.core.adas.PacejkaPowerSteeringModule;
import ch.ethz.idsc.gokart.core.adas.SetVelSmartBrakingModule;
import ch.ethz.idsc.gokart.core.adas.SpeedLimitPerSectionModule;
import ch.ethz.idsc.gokart.core.adas.SteerVibrationModule;
import ch.ethz.idsc.gokart.core.fuse.AutonomousSafetyModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotCoolingModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotSafetyModule;
import ch.ethz.idsc.gokart.core.fuse.LinmotTakeoverModule;
import ch.ethz.idsc.gokart.core.fuse.LocalizationEmergencyModule;
import ch.ethz.idsc.gokart.core.fuse.MiscEmergencyWatchdog;
import ch.ethz.idsc.gokart.core.fuse.RadioEmergencyModule;
import ch.ethz.idsc.gokart.core.fuse.SpeedLimitSafetyModule;
import ch.ethz.idsc.gokart.core.fuse.SteerBatteryWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SteerCalibrationWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SteerPassiveModule;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.core.fuse.Vmu931CalibrationWatchdog;
import ch.ethz.idsc.gokart.core.fuse.Vmu931ReadingWatchdog;
import ch.ethz.idsc.gokart.core.man.AutomaticPowerTestModule;
import ch.ethz.idsc.gokart.core.man.DriftThrustManualModule;
import ch.ethz.idsc.gokart.core.man.LookupTableRimoThrustManualModule;
import ch.ethz.idsc.gokart.core.man.ManualResetModule;
import ch.ethz.idsc.gokart.core.man.PredictiveTorqueVectoringModule;
import ch.ethz.idsc.gokart.core.man.RimoThrustManualModule;
import ch.ethz.idsc.gokart.core.map.OccupancyMappingModule;
import ch.ethz.idsc.gokart.core.map.OccupancyViewerModule;
import ch.ethz.idsc.gokart.core.mpc.LudicControlModule;
import ch.ethz.idsc.gokart.core.mpc.MPCDrivingDynamicModule;
import ch.ethz.idsc.gokart.core.mpc.MPCDrivingKinematicModule;
import ch.ethz.idsc.gokart.core.mpc.MPCDrivingTorqueModule;
import ch.ethz.idsc.gokart.core.plan.ClothoidRrtsTrajectoryModule;
import ch.ethz.idsc.gokart.core.plan.ClothoidTrajectoryModule;
import ch.ethz.idsc.gokart.core.plan.DubinsRrtsTrajectoryModule;
import ch.ethz.idsc.gokart.core.plan.PureRrtsTrajectoryModule;
import ch.ethz.idsc.gokart.core.plan.PureTrajectoryModule;
import ch.ethz.idsc.gokart.core.pos.PoseLcmServerModule;
import ch.ethz.idsc.gokart.core.pure.CenterLinePursuitModule;
import ch.ethz.idsc.gokart.core.pure.FigureClothoidModule;
import ch.ethz.idsc.gokart.core.pure.FigurePureModule;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.core.sound.GokartSoundLcmModule;
import ch.ethz.idsc.gokart.core.sound.VoiceOutputModule;
import ch.ethz.idsc.gokart.core.track.TrackReconModule;
import ch.ethz.idsc.gokart.dev.GokartTimestampModule;
import ch.ethz.idsc.gokart.dev.mcusb.McUsbModule;
import ch.ethz.idsc.gokart.dev.u3.LabjackU3Module;
import ch.ethz.idsc.gokart.gui.lab.AutoboxTestingModule;
import ch.ethz.idsc.gokart.gui.lab.IgnitionModule;
import ch.ethz.idsc.gokart.gui.lab.LinmotConstantPressTestModule;
import ch.ethz.idsc.gokart.gui.lab.LinmotPressTestModule;
import ch.ethz.idsc.gokart.gui.lab.SteerSignalModule;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.gokart.gui.top.LocalViewLcmModule;
import ch.ethz.idsc.gokart.gui.top.PresenterLcmModule;
import ch.ethz.idsc.gokart.gui.top.SideViewLcmModule;
import ch.ethz.idsc.gokart.gui.trj.TrajectoryDesignModule;
import ch.ethz.idsc.gokart.lcm.LoggerModule;
import ch.ethz.idsc.gokart.lcm.SpyModule;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931LcmServerModule;
import ch.ethz.idsc.gokart.gui.led.VirtualLedModule;
import ch.ethz.idsc.gokart.gui.led.KittLedModule;
import ch.ethz.idsc.gokart.gui.led.KittLedModuleCoach;
import ch.ethz.idsc.gokart.lcm.mod.AutoboxLcmServerModule;
import ch.ethz.idsc.gokart.lcm.mod.Vlp16PosLcmServerModule;
import ch.ethz.idsc.gokart.lcm.mod.Vlp16RayLcmServerModule;
import ch.ethz.idsc.retina.app.slam.online.DavisSlamLidarModule;
import ch.ethz.idsc.retina.app.slam.online.DavisSlamOdometryModule;
import ch.ethz.idsc.retina.app.slam.online.DavisSlamVisualModule;
import ch.ethz.idsc.retina.app.slam.online.SEyeSlamLidarModule;
import ch.ethz.idsc.retina.app.slam.online.SEyeSlamOdometryModule;
import ch.ethz.idsc.retina.app.slam.online.SEyeSlamVisualModule;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.TabbedTaskGui;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.io.ResourceData;

/** RunTabbedTaskGui is a program that is typically for offline processing.
 * The window in a convenient way to launch files. */
/* package */ enum RunTabbedTaskGui {
  ;
  /** file contains plain text brief description of modules */
  static final Properties PROPERTIES = //
      ResourceData.properties("/gui/properties/modules_description.properties");
  // ---
  static final List<Class<? extends AbstractModule>> MODULES_DEV = Arrays.asList( //
      AutoboxSocketModule.class, // sensing and actuation
      Vlp16RayLcmServerModule.class, // sensing lidar
      Vlp16PosLcmServerModule.class, // sensing gps
      AutoboxLcmServerModule.class, //
      SteerColumnLcmModule.class, //
      GokartTimestampModule.class, //
      KittLedModule.class, //
      KittLedModuleCoach.class, //
      VirtualLedModule.class, //
      LoggerModule.class, //
      LabjackU3Module.class, //
      SteerCalibrationWatchdog.class, // <- DON'T REMOVE
      MiscEmergencyWatchdog.class, // <- DON'T REMOVE
      SteerPassiveModule.class, //
      LinmotSafetyModule.class, //
      Vmu931CalibrationWatchdog.class, //
      Vmu931ReadingWatchdog.class, //
      Vlp16PassiveSlowing.class, //
      LidarLocalizationModule.class, //
      /* pose lcm server has to come after lidar localization module */
      PoseLcmServerModule.class, // publishes pose
      LocalizationEmergencyModule.class, //
      ManualResetModule.class, //
      AutonomousSafetyModule.class //
  );
  static final List<Class<? extends AbstractModule>> MODULES_CFG = Arrays.asList( //
      Vmu931LcmServerModule.class, // vmu931 imu
      McUsbModule.class, //
      // Vmu932LcmServerModule.class, // vmu932 imu
      IgnitionModule.class, // actuation monitoring
      GlobalViewLcmModule.class, // initialize localization
      TrajectoryDesignModule.class, //
      TrackReconModule.class, //
      LocalViewLcmModule.class, //
      ParametersModule.class, // configure parameters
      // SeesLcmModule.class, //
      GokartSoundLcmModule.class, //
      VoiceOutputModule.class //
  );
  static final List<Class<? extends AbstractModule>> MODULES_MAN = Arrays.asList( //
      PredictiveTorqueVectoringModule.class, //
      NaivePowerSteeringModule.class, //
      PacejkaPowerSteeringModule.class, //
      LaneKeepingLimitedSteeringModule.class, //
      NoFrictionExperiment.class, //
      SteerVibrationModule.class, //
      ConstantTorqueSteerModule.class, //
      AntilockBrakeModule.class, //
      SetVelSmartBrakingModule.class, //
      SpeedLimitPerSectionModule.class, //
      AutomaticPowerTestModule.class, //
      RimoThrustManualModule.class, //
      LookupTableRimoThrustManualModule.class, //
      DriftThrustManualModule.class //
  );
  static final List<Class<? extends AbstractModule>> MODULES_AUT = Arrays.asList( //
      FigureClothoidModule.class, //
      FigurePureModule.class, //
      MPCDrivingDynamicModule.class, //
      MPCDrivingTorqueModule.class, //
      // MPCDrivingLudicModule.class, //
      LudicControlModule.class, //
      MPCDrivingKinematicModule.class, //
      PureTrajectoryModule.class, //
      ClothoidTrajectoryModule.class, //
      PureRrtsTrajectoryModule.class, //
      ClothoidRrtsTrajectoryModule.class, //
      DubinsRrtsTrajectoryModule.class, //
      CenterLinePursuitModule.class, //
      DavisSlamLidarModule.class, //
      DavisSlamVisualModule.class, //
      DavisSlamOdometryModule.class, //
      SEyeSlamLidarModule.class, //
      SEyeSlamOdometryModule.class, //
      SEyeSlamVisualModule.class //
  );
  static final List<Class<? extends AbstractModule>> MODULES_FUSE = Arrays.asList( //
      RadioEmergencyModule.class, //
      OccupancyMappingModule.class, //
      OccupancyViewerModule.class, //
      SpeedLimitSafetyModule.class, //
      SteerBatteryWatchdog.class, //
      LinmotCoolingModule.class, //
      LinmotTakeoverModule.class //
  );
  static final List<Class<? extends AbstractModule>> MODULES_LAB = Arrays.asList( //
      SpyModule.class, //
      AutoboxTestingModule.class, //
      SteerSignalModule.class, //
      LinmotPressTestModule.class, //
      LinmotConstantPressTestModule.class, //
      // LocalViewLcmModule.class, //
      DavisDetailModule.class, //
      SeyeDetailModule.class, //
      PanoramaViewModule.class, // , //
      SideViewLcmModule.class, //
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
