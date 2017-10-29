// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Arrays;

import ch.ethz.idsc.retina.gui.gokart.top.LocalViewLcmModule;
import ch.ethz.idsc.retina.lcm.mod.AutoboxLcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Hdl32eLcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Mark8LcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Urg04lxLcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Vlp16LcmServerModule;
import ch.ethz.idsc.retina.sys.LoggerModule;
import ch.ethz.idsc.retina.sys.SpyModule;
import ch.ethz.idsc.retina.sys.TabbedTaskGui;

enum RunTabbedTaskGui {
  ;
  public static void main(String[] args) {
    TabbedTaskGui taskTabGui = new TabbedTaskGui();
    taskTabGui.tab("sensor", Arrays.asList( //
        Hdl32eLcmServerModule.class, //
        Vlp16LcmServerModule.class, //
        Mark8LcmServerModule.class, //
        Urg04lxLcmServerModule.class //
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
        JoystickSimpleDriveModule.class, //
        JoystickFullControlModule.class, //
        LocalViewLcmModule.class //
    ));
    taskTabGui.jFrame.setVisible(true);
  }
}
