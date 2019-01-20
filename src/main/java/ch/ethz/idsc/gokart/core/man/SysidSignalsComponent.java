// code by az
package ch.ethz.idsc.gokart.core.man;

import java.awt.Dimension;

import javax.swing.JToolBar;

import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.app.util.SpinnerListener;

/* package */ class SysidSignalsComponent extends ToolbarsComponent implements SpinnerListener<SysidSignals> {
  private static final SysidSignals DEFAULT = SysidSignals.CHIRP_SLOW;
  // ---
  private final SpinnerLabel<SysidSignals> spinnerLabelSignals = new SpinnerLabel<>();
  final SysidRimoModule sysidRimoModule = new SysidRimoModule();

  public SysidSignalsComponent() {
    spinnerLabelSignals.setArray(SysidSignals.values());
    spinnerLabelSignals.setValue(DEFAULT);
    sysidRimoModule.setSignal(DEFAULT.get());
    spinnerLabelSignals.addSpinnerListener(this);
    {
      JToolBar jToolBar = createRow("Input signal");
      spinnerLabelSignals.addToComponentReduced( //
          jToolBar, new Dimension(200, 28), "Select input signal for rimo");
    }
  }

  @Override
  public void actionPerformed(SysidSignals sysidSignals) {
    sysidRimoModule.setSignal(sysidSignals.get());
  }
}
