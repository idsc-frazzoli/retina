// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocketModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.LinmotCoolingModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.LinmotEmergencyModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.LinmotTakeoverModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.MiscEmergencyModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.SteerEmergencyModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.Urg04lxClearanceModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.Urg04lxEmergencyModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.Vlp16ClearanceModule;
import ch.ethz.idsc.retina.dev.zhkart.joy.DeadManSwitchModule;
import ch.ethz.idsc.retina.dev.zhkart.joy.LinmotJoystickModule;
import ch.ethz.idsc.retina.dev.zhkart.joy.RimoTorqueJoystickModule;
import ch.ethz.idsc.retina.dev.zhkart.joy.SteerJoystickModule;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseLcmModule;
import ch.ethz.idsc.retina.gui.gokart.crv.CurveFollowerModule;
import ch.ethz.idsc.retina.gui.gokart.lab.AutoboxTestingModule;
import ch.ethz.idsc.retina.gui.gokart.top.GlobalViewLcmModule;
import ch.ethz.idsc.retina.gui.gokart.top.LocalViewLcmModule;
import ch.ethz.idsc.retina.lcm.mod.AutoboxLcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Urg04lxLcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Vlp16LcmServerModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.sys.LoggerModule;
import ch.ethz.idsc.retina.sys.SpyModule;
import ch.ethz.idsc.retina.sys.TabbedTaskGui;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

enum RunTabbedTaskGui {
  ;
  static final List<Class<?>> MODULES_DEV = Arrays.asList( //
      AutoboxSocketModule.class, // sensing and actuation
      Vlp16LcmServerModule.class, // sensing
      AutoboxLcmServerModule.class, //
      GokartStatusLcmModule.class, //
      LoggerModule.class //
  );
  static final List<Class<?>> MODULES_LAB = Arrays.asList( //
      Urg04lxLcmServerModule.class, // sensing TODO move back to DEV list
      GokartPoseLcmModule.class, // move to DEV list
      SpyModule.class, //
      ParametersModule.class, //
      AutoboxIntrospectionModule.class, //
      AutoboxTestingModule.class, //
      LocalViewLcmModule.class, //
      GlobalViewLcmModule.class, //
      DavisDetailModule.class, //
      PanoramaViewModule.class, //
      DavisOverviewModule.class //
  );
  static final List<Class<?>> MODULES_FUSE = Arrays.asList( //
      Urg04lxEmergencyModule.class, //
      MiscEmergencyModule.class, //
      SteerEmergencyModule.class, //
      LinmotEmergencyModule.class, //
      LinmotCoolingModule.class, //
      LinmotTakeoverModule.class, //
      Vlp16ClearanceModule.class, //
      Urg04lxClearanceModule.class //
  );
  static final List<Class<?>> MODULES_JOY = Arrays.asList( //
      DeadManSwitchModule.class, // joystick
      LinmotJoystickModule.class, //
      SteerJoystickModule.class, //
      RimoTorqueJoystickModule.class //
  );
  static final List<Class<?>> MODULES_AUT = Arrays.asList( //
      CurveFollowerModule.class //
  );

  public static void main(String[] args) {
    WindowConfiguration wc = AppCustomization.load(RunTabbedTaskGui.class, new WindowConfiguration());
    TabbedTaskGui taskTabGui = new TabbedTaskGui();
    taskTabGui.tab("dev", MODULES_DEV);
    taskTabGui.tab("lab", MODULES_LAB);
    taskTabGui.tab("fuse", MODULES_FUSE);
    taskTabGui.tab("joy", MODULES_JOY);
    taskTabGui.tab("aut", MODULES_AUT);
    wc.attach(RunTabbedTaskGui.class, taskTabGui.jFrame);
    taskTabGui.jFrame.setVisible(true);
  }
}
