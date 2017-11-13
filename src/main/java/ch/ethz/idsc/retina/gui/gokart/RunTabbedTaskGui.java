// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Arrays;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocketModule;
import ch.ethz.idsc.retina.gui.gokart.top.LocalViewLcmModule;
import ch.ethz.idsc.retina.lcm.mod.AutoboxLcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Hdl32eLcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Mark8LcmServerModule;
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
    taskTabGui.tab("devices", Arrays.asList( //
        Hdl32eLcmServerModule.class, //
        Vlp16LcmServerModule.class, //
        Mark8LcmServerModule.class, //
        Urg04lxLcmServerModule.class, //
        AutoboxSocketModule.class //
    ));
    taskTabGui.tab("lcm", Arrays.asList( //
        AutoboxLcmServerModule.class, //
        GokartStatusLcmModule.class, //
        LoggerModule.class //
    ));
    taskTabGui.tab("watchdog", Arrays.asList( //
        Urg04lxEmergencyModule.class, //
        MiscEmergencyModule.class, //
        RimoEmergencyModule.class, //
        LinmotEmergencyModule.class, //
        LinmotTakeoverModule.class //
    ));
    taskTabGui.tab("gui", Arrays.asList( //
        SpyModule.class, //
        ParametersModule.class, //
        AutoboxTestingModule.class, //
        RimoMetronomeModule.class, //
        AutboxProviderModule.class, //
        JoystickSimpleDriveModule.class, //
        JoystickFullControlModule.class, //
        LocalViewLcmModule.class, //
        PanoramaViewModule.class, //
        DavisOverviewModule.class, //
        DavisDetailModule.class //
    ));
    wc.attach(RunTabbedTaskGui.class, taskTabGui.jFrame);
    taskTabGui.jFrame.setVisible(true);
  }
}
