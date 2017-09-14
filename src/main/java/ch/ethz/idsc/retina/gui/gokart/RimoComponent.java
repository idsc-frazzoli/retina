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

import ch.ethz.idsc.retina.dev.rimo.RimoDevice;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.util.HexStrings;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public class RimoComponent extends InterfaceComponent {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createShort("OPERATION", (short) 0x0009) //
  );
  // ---
  private final byte[] data_receive = new byte[2 * 14];
  private final DatagramSocketManager datagramSocketManager = //
      DatagramSocketManager.local(data_receive, RimoDevice.LOCAL_PORT, RimoDevice.LOCAL_ADDRESS);
  private TimerTask timerTask = null;
  private final SpinnerLabel<Word> spinnerLabelLw = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelRw = new SpinnerLabel<>();
  private final SliderExt sliderExtLs;
  private final SliderExt sliderExtRs;

  public RimoComponent() {
    // LEFT
    {
      JToolBar jToolBar = createRow("LEFT command");
      spinnerLabelLw.setList(COMMANDS);
      spinnerLabelLw.setValueSafe(COMMANDS.get(0));
      spinnerLabelLw.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("LEFT speed");
      sliderExtLs = SliderExt.wrap(new JSlider(-8000, 8000, 0));
      sliderExtLs.addToComponent(jToolBar);
    }
    // RIGHT
    {
      JToolBar jToolBar = createRow("RIGHT command");
      spinnerLabelRw.setList(COMMANDS);
      spinnerLabelRw.setValueSafe(COMMANDS.get(0));
      spinnerLabelRw.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("RIGHT speed");
      sliderExtRs = SliderExt.wrap(new JSlider(-8000, 8000, 0));
      sliderExtRs.addToComponent(jToolBar);
    }
  }

  @Override
  public void connectAction(int period, boolean isSelected) {
    if (isSelected) {
      datagramSocketManager.start();
      timerTask = new TimerTask() {
        @Override
        public void run() {
          final byte data[] = new byte[8];
          final ByteBuffer byteBuffer = ByteBuffer.wrap(data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          byteBuffer.position(0);
          {
            RimoPutEvent rimoPutEvent = new RimoPutEvent();
            rimoPutEvent.command = spinnerLabelLw.getValue().getShort();
            rimoPutEvent.speed = (short) sliderExtLs.jSlider.getValue();
            rimoPutEvent.insert(byteBuffer);
          }
          {
            RimoPutEvent rimoPutEvent = new RimoPutEvent();
            rimoPutEvent.command = spinnerLabelRw.getValue().getShort();
            rimoPutEvent.speed = (short) sliderExtRs.jSlider.getValue();
            rimoPutEvent.insert(byteBuffer);
          }
          System.out.println("rimo put=" + HexStrings.from(data));
          try {
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, //
                InetAddress.getByName(RimoDevice.REMOTE_ADDRESS), RimoDevice.REMOTE_PORT);
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
  public String connectionInfoRemote() {
    return String.format("%s:%d", RimoDevice.REMOTE_ADDRESS, RimoDevice.REMOTE_PORT);
  }

  @Override
  public String connectionInfoLocal() {
    return String.format("%s:%d", RimoDevice.LOCAL_ADDRESS, RimoDevice.LOCAL_PORT);
  }
}
