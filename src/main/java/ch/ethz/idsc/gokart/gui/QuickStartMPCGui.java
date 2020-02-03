// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.core.adas.NaivePowerSteeringModule;
import ch.ethz.idsc.gokart.core.man.PredictiveTorqueVectoringModule;
import ch.ethz.idsc.gokart.core.mpc.LudicControlModule;
import ch.ethz.idsc.gokart.core.track.TrackReconModule;
import ch.ethz.idsc.gokart.gui.lab.IgnitionModule;
import ch.ethz.idsc.gokart.gui.led.KittLedModule;
import ch.ethz.idsc.gokart.gui.led.VirtualLedModule;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931LcmServerModule;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.TabbedTaskGui;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

/** Important: QuickStartGui only works when connected to the real gokart hardware. */
/* package */ enum QuickStartMPCGui {
  ;

  static final List<Class<? extends AbstractModule>> MODULES_CFG_MIN = Arrays.asList( //
      Vmu931LcmServerModule.class, // vmu931 imu
      // Vmu932LcmServerModule.class, // vmu932 imu
      IgnitionModule.class, // actuation monitoring
      KittLedModule.class,
      GlobalViewLcmModule.class // initialize localization
      // TrackReconModule.class, //
      // ParametersModule.class // configure parameters
  );
  static final List<Class<? extends AbstractModule>> MODULES_TRACK = Arrays.asList( //
      TrackReconModule.class, //
      PredictiveTorqueVectoringModule.class, //
      NaivePowerSteeringModule.class //
  );
  static final List<Class<? extends AbstractModule>> MODULES_MPC_MIN = Arrays.asList( //
      // PredictiveTorqueVectoringModule.class, //
      // NaivePowerSteeringModule.class, //
      LudicControlModule.class, //
      VirtualLedModule.class, //
      ParametersModule.class // configure parameters
  );

  public static void main(String[] args) {
    WindowConfiguration windowConfiguration = //
        AppCustomization.load(QuickStartMPCGui.class, new WindowConfiguration());
    ModuleAuto.INSTANCE.runAll(RunTabbedTaskGui.MODULES_DEV);
    TabbedTaskGui taskTabGui = new TabbedTaskGui(RunTabbedTaskGui.PROPERTIES);
    // ---
    taskTabGui.tab("cfg", QuickStartMPCGui.MODULES_CFG_MIN);
    taskTabGui.tab("track", QuickStartMPCGui.MODULES_TRACK);
    taskTabGui.tab("mpc", QuickStartMPCGui.MODULES_MPC_MIN);
    windowConfiguration.attach(QuickStartMPCGui.class, taskTabGui.jFrame);
    taskTabGui.jFrame.setVisible(true);
  }
}
