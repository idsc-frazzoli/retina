// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.JTextField;

import ch.ethz.idsc.retina.dev.linmot.LinmotDatagramClient;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;

public class LinmotGetComponent extends InterfaceComponent implements LinmotGetListener {
  private static final int PORT = 12333;
  // ---
  private final JTextField reading;
  LinmotDatagramClient linmotDatagramClient = new LinmotDatagramClient(PORT);

  public LinmotGetComponent() {
    reading = createReading("receive");
  }

  @Override
  public void linmotGet(LinmotGetEvent linmotGetEvent) {
    reading.setText(linmotGetEvent.toInfoString());
  }

  @Override
  public void connectAction(int period, boolean isSelected) {
    if (isSelected)
      linmotDatagramClient.start();
    else
      linmotDatagramClient.stop();
  }

  @Override
  public String connectionInfo() {
    return "" + PORT;
  }
}
