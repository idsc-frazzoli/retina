// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.TabbedTaskGui;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

/** Important: QuickStartGui only works when connected to the real gokart hardware. */
/* package */ enum QuickStartMPCGui {
  ;
  public static void main(String[] args) {
    WindowConfiguration windowConfiguration = //
        AppCustomization.load(QuickStartMPCGui.class, new WindowConfiguration());
    ModuleAuto.INSTANCE.runAll(RunTabbedTaskGui.MODULES_DEV);
    TabbedTaskGui taskTabGui = new TabbedTaskGui(RunTabbedTaskGui.PROPERTIES);
    // ---
    taskTabGui.tab("cfg", RunTabbedTaskGui.MODULES_CFG_MIN);
    taskTabGui.tab("track", RunTabbedTaskGui.MODULES_TRACK);
    taskTabGui.tab("mpc", RunTabbedTaskGui.MODULES_MPC_MIN);
    windowConfiguration.attach(QuickStartMPCGui.class, taskTabGui.jFrame);
    taskTabGui.jFrame.setVisible(true);
  }
}