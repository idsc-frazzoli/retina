//// code by jph
// package ch.ethz.idsc.retina.gui.gokart;
//
// import javax.swing.JTextField;
//
// import ch.ethz.idsc.retina.dev.rimo.RimoGetDatagramClient;
// import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
// import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
//
// public class RimoGetComponent extends InterfaceComponent implements RimoGetListener {
// private static final int PORT = 5000;
// // public static final String GROUP = "localhost";
// public static final String GROUP = "192.168.1.1";
// // public static final String GROUP = "239.255.76.67";
// // ---
// private final JTextField reading;
// RimoGetDatagramClient rimoDatagramClient = new RimoGetDatagramClient(GROUP, PORT);
//
// public RimoGetComponent() {
// reading = createReading("receive");
// rimoDatagramClient.addListener(this);
// }
//
// @Override
// public void rimoGet(RimoGetEvent rimoGetLeft, RimoGetEvent rimoGetRight) {
// System.out.println("received");
// // reading.setText(rimoGetLeft.toInfoString());
// }
//
// @Override
// public void connectAction(int period, boolean isSelected) {
// if (isSelected)
// rimoDatagramClient.start();
// else
// rimoDatagramClient.stop();
// }
//
// @Override
// public String connectionInfo() {
// return GROUP + ":" + PORT;
// }
// }
