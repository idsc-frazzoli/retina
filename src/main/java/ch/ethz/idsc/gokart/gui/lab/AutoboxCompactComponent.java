// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.tensor.sca.Round;

public class AutoboxCompactComponent extends ToolbarsComponent implements RimoGetListener {
  // TODO document for all gui elements why the subscriptions are the way they are
  final LinmotInitButton linmotInitButton = new LinmotInitButton();
  final MiscResetButton miscResetButton = new MiscResetButton();
  final SteerInitButton steerInitButton = new SteerInitButton();
  final JTextField[] jTextField = new JTextField[2];
  final JTextField jTF_joystick;
  final JTextField jTF_davis240c;

  public AutoboxCompactComponent() {
    {
      JToolBar jToolBar = createRow("Linmot");
      jToolBar.add(linmotInitButton.getComponent());
    }
    {
      JToolBar jToolBar = createRow("Misc");
      jToolBar.add(miscResetButton.getComponent());
    }
    {
      JToolBar jToolBar = createRow("Steer");
      jToolBar.add(steerInitButton.getComponent());
    }
    jTextField[0] = createReading("Rimo LEFT");
    jTextField[1] = createReading("Rimo RIGHT");
    jTF_davis240c = createReading("Davis240C");
    jTF_joystick = createReading("Joystick");
  }

  @Override
  public void getEvent(RimoGetEvent getEvent) {
    jTextField[0].setText(getEvent.getTireL.getAngularRate_Y().map(Round._3).toString());
    jTextField[1].setText(getEvent.getTireR.getAngularRate_Y().map(Round._3).toString());
  }
}
