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

import javax.swing.JSlider;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.joystick.GenericXboxPadJoystick;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickEventListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public class RimoComponent extends InterfaceComponent implements ByteArrayConsumer, RimoGetListener, JoystickEventListener {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createShort("OPERATION", (short) 0x0009) //
  );
  // ---
  private final DatagramSocketManager datagramSocketManager = //
      DatagramSocketManager.local(new byte[2 * RimoGetEvent.LENGTH], RimoSocket.LOCAL_PORT, RimoSocket.LOCAL_ADDRESS);
  private TimerTask timerTask = null;
  private final SpinnerLabel<Word> spinnerLabelLCmd = new SpinnerLabel<>();
  private final SliderExt sliderExtLVel;
  private final SpinnerLabel<Word> spinnerLabelRCmd = new SpinnerLabel<>();
  private final SliderExt sliderExtRVel;
  private final RimoGetFields rimoGetFieldsL = new RimoGetFields();
  private final RimoGetFields rimoGetFieldsR = new RimoGetFields();

  public RimoComponent() {
    datagramSocketManager.addListener(this);
    // LEFT
    {
      JToolBar jToolBar = createRow("LEFT command");
      spinnerLabelLCmd.setList(COMMANDS);
      spinnerLabelLCmd.setValueSafe(COMMANDS.get(0));
      spinnerLabelLCmd.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("LEFT speed");
      sliderExtLVel = SliderExt.wrap(new JSlider(-RimoPutEvent.MAX_SPEED, RimoPutEvent.MAX_SPEED, 0));
      sliderExtLVel.addToComponent(jToolBar);
    }
    // RIGHT
    {
      JToolBar jToolBar = createRow("RIGHT command");
      spinnerLabelRCmd.setList(COMMANDS);
      spinnerLabelRCmd.setValueSafe(COMMANDS.get(0));
      spinnerLabelRCmd.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("RIGHT speed");
      sliderExtRVel = SliderExt.wrap(new JSlider(-RimoPutEvent.MAX_SPEED, RimoPutEvent.MAX_SPEED, 0));
      sliderExtRVel.addToComponent(jToolBar);
    }
    addSeparator();
    { // reception
      assign(rimoGetFieldsL, "LEFT");
      assign(rimoGetFieldsR, "RIGHT");
    }
  }

  private void assign(RimoGetFields rimoGetFields, String side) {
    rimoGetFields.jTF_status_word = createReading(side + " status word");
    rimoGetFields.jTF_actual_speed = createReading(side + " actual speed");
    rimoGetFields.jTF_rms_motor_current = createReading(side + " rms current");
    rimoGetFields.jTF_dc_bus_voltage = createReading(side + " dc bus voltage");
    rimoGetFields.jTF_error_code = createReading(side + " error code");
    rimoGetFields.jTF_temperature_motor = createReading(side + " temp. motor");
    rimoGetFields.jTF_temperature_heatsink = createReading(side + " temp. heatsink");
  }

  @Override
  public void connectAction(int period, boolean isSelected) {
    if (isSelected) {
      datagramSocketManager.start();
      timerTask = new TimerTask() {
        @Override
        public void run() {
          byte data[] = new byte[2 * RimoPutEvent.LENGTH];
          ByteBuffer byteBuffer = ByteBuffer.wrap(data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          {
            RimoPutEvent rimoPutEvent = new RimoPutEvent();
            rimoPutEvent.command = spinnerLabelLCmd.getValue().getShort();
            rimoPutEvent.speed = (short) sliderExtLVel.jSlider.getValue();
            rimoPutEvent.insert(byteBuffer);
          }
          {
            RimoPutEvent rimoPutEvent = new RimoPutEvent();
            rimoPutEvent.command = spinnerLabelRCmd.getValue().getShort();
            rimoPutEvent.speed = (short) sliderExtRVel.jSlider.getValue();
            rimoPutEvent.insert(byteBuffer);
          }
          // System.out.println("rimo put=" + HexStrings.from(data));
          try {
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, //
                InetAddress.getByName(RimoSocket.REMOTE_ADDRESS), RimoSocket.REMOTE_PORT);
            datagramSocketManager.send(datagramPacket);
          } catch (Exception exception) {
            // ---
            System.out.println("RIMO SEND FAIL");
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
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    try {
      RimoGetEvent rimoGetL = new RimoGetEvent(byteBuffer);
      RimoGetEvent rimoGetR = new RimoGetEvent(byteBuffer);
      rimoGet(rimoGetL, rimoGetR);
    } catch (Exception exception) {
      System.out.println("fail decode RimoGet, received=" + length);
      // TODO: handle exception
    }
  }

  @Override
  public void rimoGet(RimoGetEvent rimoGetL, RimoGetEvent rimoGetR) {
    rimoGetFieldsL.updateText(rimoGetL);
    rimoGetFieldsR.updateText(rimoGetR);
  }

  @Override
  public String connectionInfoRemote() {
    return String.format("%s:%d", RimoSocket.REMOTE_ADDRESS, RimoSocket.REMOTE_PORT);
  }

  @Override
  public String connectionInfoLocal() {
    return String.format("%s:%d", RimoSocket.LOCAL_ADDRESS, RimoSocket.LOCAL_PORT);
  }

  private int sign = 1;

  @Override
  public void joystick(JoystickEvent joystickEvent) {
    if (joystickEnabled) {
      GenericXboxPadJoystick joystick = (GenericXboxPadJoystick) joystickEvent;
      if (joystick.isButtonPressedBack()) {
        sign = -1;
      }
      if (joystick.isButtonPressedStart()) {
        sign = 1;
      }
      double wheelL = joystick.getLeftSliderUnitValue();
      sliderExtLVel.jSlider.setValue((int) (wheelL * RimoPutEvent.MAX_SPEED * sign));
      double wheelR = joystick.getRightSliderUnitValue();
      sliderExtRVel.jSlider.setValue((int) (wheelR * RimoPutEvent.MAX_SPEED * sign));
    }
  }
}
