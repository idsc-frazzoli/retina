// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

import javax.swing.JSlider;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.steer.SteerDevice;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.retina.util.io.UniversalDatagramPublisher;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.Round;

public class SteerPutComponent extends InterfaceComponent {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createByte("OFF", (byte) 0), //
      Word.createByte("ON", (byte) 1) //
  );
  // ---
  private final byte data[] = new byte[8];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(data);
  private final UniversalDatagramPublisher universalDatagramPublisher = //
      new UniversalDatagramPublisher(data, AutoboxDevice.GROUP, SteerDevice.PORT);
  //
  private final SpinnerLabel<Word> spinnerLabelLw = new SpinnerLabel<>();
  private final SliderExt sliderExtLs;

  public SteerPutComponent() {
    // LEFT
    {
      JToolBar jToolBar = createRow("command");
      spinnerLabelLw.setList(COMMANDS);
      spinnerLabelLw.setValueSafe(COMMANDS.get(0));
      spinnerLabelLw.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("torque");
      sliderExtLs = SliderExt.wrap(new JSlider(-5000, 5000, 0)); // values are divided by 1000
      sliderExtLs.physics = scalar -> scalar.multiply(RealScalar.of(1e-3)).map(Round._4).Get();
      sliderExtLs.addToComponent(jToolBar);
    }
  }

  private TimerTask timerTask = null;

  @Override
  public void connectAction(int period, boolean isSelected) {
    if (isSelected) {
      timerTask = new TimerTask() {
        @Override
        public void run() {
          SteerPutEvent steerPutEvent = new SteerPutEvent();
          steerPutEvent.command = spinnerLabelLw.getValue().getByte();
          steerPutEvent.torque = sliderExtLs.jSlider.getValue() * 1e-3f;
          byteBuffer.position(0);
          steerPutEvent.insert(byteBuffer);
          universalDatagramPublisher.send();
        }
      };
      timer.schedule(timerTask, 100, period);
    } else {
      if (Objects.nonNull(timerTask)) {
        timerTask.cancel();
        timerTask = null;
      }
    }
  }

  @Override
  public String connectionInfoRemote() {
    return String.format("%s:%d", AutoboxDevice.GROUP, SteerDevice.PORT);
  }

  @Override
  public String connectionInfoLocal() {
    // TODO Auto-generated method stub
    return "";
  }
}
