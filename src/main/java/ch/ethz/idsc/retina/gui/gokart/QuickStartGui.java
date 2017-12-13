// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.sys.ModuleAuto;
import ch.ethz.idsc.retina.sys.TabbedTaskGui;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

/** important: QuickStartGui only works when connected to the real gokart hardware */
enum QuickStartGui {
  ;
  public static void main(String[] args) {
    WindowConfiguration wc = AppCustomization.load(QuickStartGui.class, new WindowConfiguration());
    ModuleAuto.INSTANCE.runAll(RunTabbedTaskGui.MODULES_DEV);
    ModuleAuto.INSTANCE.runAll(RunTabbedTaskGui.MODULES_LCM);
    TabbedTaskGui taskTabGui = new TabbedTaskGui();
    // ---
    taskTabGui.tab("lab", RunTabbedTaskGui.MODULES_LAB);
    taskTabGui.tab("lab", RunTabbedTaskGui.MODULES_FUSE);
    taskTabGui.tab("lab", RunTabbedTaskGui.MODULES_TRACK);
    wc.attach(QuickStartGui.class, taskTabGui.jFrame);
    taskTabGui.jFrame.setVisible(true);
  }
}
