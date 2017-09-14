// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.JTextField;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetDatagramClient;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;

public class LinmotGetComponent extends InterfaceComponent implements LinmotGetListener {
  private static final int PORT = 5001;
  // public static final String GROUP = "localhost";
  public static final String GROUP = "192.168.1.1";
  // public static final String GROUP = "239.255.76.67";
  // ---
  private final JTextField reading;
  LinmotGetDatagramClient linmotDatagramClient = new LinmotGetDatagramClient(GROUP, PORT);

  public LinmotGetComponent() {
    reading = createReading("receive");
    linmotDatagramClient.addListener(this);
  }

  @Override
  public void linmotGet(LinmotGetEvent linmotGetEvent) {
    System.out.println("received");
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
    return GROUP + ":" + PORT;
  }
}
