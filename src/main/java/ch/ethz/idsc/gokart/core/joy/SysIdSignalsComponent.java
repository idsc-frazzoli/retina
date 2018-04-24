package ch.ethz.idsc.gokart.core.joy;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.retina.util.gui.SpinnerListener;

public class SysIdSignalsComponent extends ToolbarsComponent implements ActionListener {
  SpinnerLabel<SysIdRimo> signals;
  JToggleButton enable = new JToggleButton("enable");
  SysidRimoModule sysidRimoModule = new SysidRimoModule();

  public SysIdSignalsComponent() {
    signals = new SpinnerLabel<>();
    signals.setArray(SysIdRimo.values());
    signals.setValue(SysIdRimo.CHIRP_SLOW);
    signals.addSpinnerListener(new SpinnerListener<SysIdRimo>() {
      @Override
      public void actionPerformed(SysIdRimo myType) {
        sysidRimoModule.set(myType.get());
      }
    });
    enable.addActionListener(this);
    {
      JToolBar jToolBar = createRow("Input signal");
      signals.addToComponent(jToolBar, new Dimension(200, 28), "Select input signal for rimo");
      jToolBar.add(enable);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (enable.isSelected()) {
      try {
        sysidRimoModule.first();
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    } else {
      sysidRimoModule.last();
    }
  }
}
