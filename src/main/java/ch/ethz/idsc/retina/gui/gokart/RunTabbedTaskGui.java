// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Arrays;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocketModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.LinmotEmergencyModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.LinmotTakeoverModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.MiscEmergencyModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.SteerEmergencyModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.Urg04lxClearanceModule;
import ch.ethz.idsc.retina.dev.zhkart.fuse.Urg04lxEmergencyModule;
import ch.ethz.idsc.retina.dev.zhkart.joy.LinmotJoystickModule;
import ch.ethz.idsc.retina.dev.zhkart.joy.RimoJoystickModule;
import ch.ethz.idsc.retina.dev.zhkart.joy.SteerJoystickModule;
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
  public static void main(String[] args) {
    WindowConfiguration wc = AppCustomization.load(RunTabbedTaskGui.class, new WindowConfiguration());
    TabbedTaskGui taskTabGui = new TabbedTaskGui();
    taskTabGui.tab("dev", Arrays.asList( //
        AutoboxSocketModule.class, // sensing and actuation
        Vlp16LcmServerModule.class, // sensing
        Urg04lxLcmServerModule.class // sensing
    ));
    taskTabGui.tab("lcm", Arrays.asList( //
        AutoboxLcmServerModule.class, //
        GokartStatusLcmModule.class, //
        LoggerModule.class //
    ));
    taskTabGui.tab("lab", Arrays.asList( //
        SpyModule.class, //
        ParametersModule.class, //
        AutoboxTestingModule.class, //
        RimoMetronomeModule.class, //
        LocalViewLcmModule.class, //
        PanoramaViewModule.class, //
        DavisOverviewModule.class, //
        DavisDetailModule.class //
    ));
    taskTabGui.tab("fuse", Arrays.asList( //
        Urg04lxEmergencyModule.class, //
        MiscEmergencyModule.class, //
        SteerEmergencyModule.class, //
        LinmotEmergencyModule.class, //
        LinmotTakeoverModule.class, //
        Urg04lxClearanceModule.class //
    ));
    taskTabGui.tab("track", Arrays.asList( //
        AutoboxIntrospectionModule.class, //
        LinmotJoystickModule.class, //
        SteerJoystickModule.class, //
        RimoJoystickModule.class //
    ));
    wc.attach(RunTabbedTaskGui.class, taskTabGui.jFrame);
    taskTabGui.jFrame.setVisible(true);
  }
}
