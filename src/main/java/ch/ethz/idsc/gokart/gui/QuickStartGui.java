// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.TabbedTaskGui;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

/** Important: QuickStartGui only works when connected to the real gokart hardware.
 * 
 * the setup procedure for joystick control is detailed in
 * doc/gokart_software_setup.md */
enum QuickStartGui {
  ;
  public static void main(String[] args) {
    WindowConfiguration wc = AppCustomization.load(QuickStartGui.class, new WindowConfiguration());
    ModuleAuto.INSTANCE.runAll(RunTabbedTaskGui.MODULES_DEV);
    TabbedTaskGui taskTabGui = new TabbedTaskGui(RunTabbedTaskGui.PROPERTIES);
    // ---
    taskTabGui.tab("cfg", RunTabbedTaskGui.MODULES_CFG);
    taskTabGui.tab("joy", RunTabbedTaskGui.MODULES_MAN);
    taskTabGui.tab("aut", RunTabbedTaskGui.MODULES_AUT);
    taskTabGui.tab("fuse", RunTabbedTaskGui.MODULES_FUSE);
    taskTabGui.tab("lab", RunTabbedTaskGui.MODULES_LAB);
    wc.attach(QuickStartGui.class, taskTabGui.jFrame);
    taskTabGui.jFrame.setVisible(true);
  }
}