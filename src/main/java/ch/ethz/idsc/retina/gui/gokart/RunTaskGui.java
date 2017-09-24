// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocketModule;
import ch.ethz.idsc.retina.lcm.mod.AutoboxGetLcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Hdl32eLcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Mark8LcmServerModule;
import ch.ethz.idsc.retina.lcm.mod.Urg04lxLcmServerModule;
import ch.ethz.idsc.retina.sys.LoggerModule;
import ch.ethz.idsc.retina.sys.SpyModule;
import ch.ethz.idsc.retina.sys.TaskGui;

enum RunTaskGui {
  ;
  public static void main(String[] args) {
    List<Class<?>> modules = new ArrayList<>();
    modules.add(SpyModule.class);
    modules.add(LoggerModule.class);
    // ---
    modules.add(Hdl32eLcmServerModule.class);
    modules.add(Mark8LcmServerModule.class);
    modules.add(Urg04lxLcmServerModule.class);
    // ---
    modules.add(Urg04lxEmergencyModule.class);
    modules.add(AutoboxGetLcmServerModule.class);
    modules.add(AutoboxTestingModule.class);
    modules.add(AutoboxJoystickModule.class);
    modules.add(AutoboxSocketModule.class);
    new TaskGui(modules);
  }
}
