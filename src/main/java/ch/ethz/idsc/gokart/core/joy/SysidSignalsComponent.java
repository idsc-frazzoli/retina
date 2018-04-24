// code by az
package ch.ethz.idsc.gokart.core.joy;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.retina.util.gui.SpinnerListener;

/* package */ class SysidSignalsComponent extends ToolbarsComponent implements ActionListener {
  private final SpinnerLabel<SysidSignals> spinnerLabelSignals = new SpinnerLabel<>();
  private final JToggleButton jToggleButtonOperation = new JToggleButton("operation");
  private final SysidRimoModule sysidRimoModule = new SysidRimoModule();

  public SysidSignalsComponent() {
    spinnerLabelSignals.setArray(SysidSignals.values());
    spinnerLabelSignals.setValue(SysidSignals.CHIRP_SLOW);
    spinnerLabelSignals.addSpinnerListener(new SpinnerListener<SysidSignals>() {
      @Override
      public void actionPerformed(SysidSignals myType) {
        sysidRimoModule.setSignal(myType.get());
      }
    });
    jToggleButtonOperation.addActionListener(this);
    {
      JToolBar jToolBar = createRow("Input signal");
      spinnerLabelSignals.addToComponentReduced( //
          jToolBar, new Dimension(200, 28), "Select input signal for rimo");
      jToolBar.add(jToggleButtonOperation);
    }
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    if (jToggleButtonOperation.isSelected())
      try {
        sysidRimoModule.first();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    else
      sysidRimoModule.last();
  }

  void shutdown() {
    if (jToggleButtonOperation.isSelected()) {
      System.out.println("shutdown signal generation");
      sysidRimoModule.last();
    }
  }
}
