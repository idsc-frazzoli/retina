// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Dimension;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.misc.MiscPutEvent;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MiscComponent extends InterfaceComponent implements ByteArrayConsumer, MiscGetListener {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createByte("PASSIVE", (byte) 0), //
      Word.createByte("RESET", (byte) 1) //
  );
  public static final List<Word> LEDCONTROL = Arrays.asList( //
      Word.createByte("OFF", (byte) 0), //
      Word.createByte("ON", (byte) 1) //
  );
  private final DatagramSocketManager datagramSocketManager = //
      DatagramSocketManager.local(new byte[MiscGetEvent.LENGTH], MiscSocket.LOCAL_PORT, MiscSocket.LOCAL_ADDRESS);
  private final SpinnerLabel<Word> spinnerLabelRimoL = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelRimoR = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelLinmot = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelSteer = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelLed = new SpinnerLabel<>();
  private final JTextField jTextFieldEmg;
  private final JTextField jTextFieldBat;
  private final JTextField jTextField;

  public MiscComponent() {
    datagramSocketManager.addListener(this);
    {
      JToolBar jToolBar = createRow("resetRimoL");
      spinnerLabelRimoL.setList(COMMANDS);
      spinnerLabelRimoL.setValueSafe(COMMANDS.get(0));
      spinnerLabelRimoL.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("resetRimoR");
      spinnerLabelRimoR.setList(COMMANDS);
      spinnerLabelRimoR.setValueSafe(COMMANDS.get(0));
      spinnerLabelRimoR.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("resetLinmot");
      spinnerLabelLinmot.setList(COMMANDS);
      spinnerLabelLinmot.setValueSafe(COMMANDS.get(0));
      spinnerLabelLinmot.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("resetSteer");
      spinnerLabelSteer.setList(COMMANDS);
      spinnerLabelSteer.setValueSafe(COMMANDS.get(0));
      spinnerLabelSteer.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("Led");
      spinnerLabelLed.setList(LEDCONTROL);
      spinnerLabelLed.setValueSafe(LEDCONTROL.get(0));
      spinnerLabelLed.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    addSeparator();
    { // reception
      jTextFieldEmg = createReading("emergency");
      jTextFieldBat = createReading("battery");
      jTextField = createReading("received");
    }
  }

  private TimerTask timerTask = null;

  @Override
  public void connectAction(int period, boolean isSelected) {
    if (isSelected) {
      datagramSocketManager.start();
      timerTask = new TimerTask() {
        @Override
        public void run() {
          MiscPutEvent miscPutEvent = new MiscPutEvent();
          miscPutEvent.resetRimoL = spinnerLabelRimoL.getValue().getByte();
          miscPutEvent.resetRimoR = spinnerLabelRimoR.getValue().getByte();
          miscPutEvent.resetLinmot = spinnerLabelLinmot.getValue().getByte();
          miscPutEvent.resetSteer = spinnerLabelSteer.getValue().getByte();
          miscPutEvent.ledControl = spinnerLabelLed.getValue().getByte();
          byte[] data = new byte[MiscPutEvent.LENGTH];
          ByteBuffer byteBuffer = ByteBuffer.wrap(data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          miscPutEvent.insert(byteBuffer);
          // System.out.println("misc put=" + HexStrings.from(data));
          try {
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, //
                InetAddress.getByName(MiscSocket.REMOTE_ADDRESS), MiscSocket.REMOTE_PORT);
            datagramSocketManager.send(datagramPacket);
          } catch (Exception exception) {
            // ---
            System.out.println("MISC SEND FAIL");
            exception.printStackTrace();
            System.exit(0); // TODO
          }
        }
      };
      timer.schedule(timerTask, 100, period);
    } else {
      if (Objects.nonNull(timerTask)) {
        timerTask.cancel();
        timerTask = null;
      }
      datagramSocketManager.stop();
    }
  }

  @Override
  public void accept(byte[] data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
    miscGet(miscGetEvent);
  }

  @Override
  public void miscGet(MiscGetEvent miscGetEvent) {
    jTextFieldEmg.setText("" + miscGetEvent.emergency);
    jTextFieldBat.setText(Quantity.of(miscGetEvent.steerBatteryVoltage(), "[V]").toString());
    jTextField.setText(miscGetEvent.toInfoString());
  }

  @Override
  public String connectionInfoRemote() {
    return String.format("%s:%d", MiscSocket.REMOTE_ADDRESS, MiscSocket.REMOTE_PORT);
  }

  @Override
  public String connectionInfoLocal() {
    return String.format("%s:%d", MiscSocket.LOCAL_ADDRESS, MiscSocket.LOCAL_PORT);
  }
}
