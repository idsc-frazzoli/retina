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

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.retina.util.io.UniversalDatagramPublisher;

public class RimoPutComponent extends InterfaceComponent {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createShort("OPERATION", (short) 0x0009) //
  );
  public static final int PORT = 5002;
  public static final String GROUP = "192.168.1.10";
  // ---
  private final byte data[] = new byte[8];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(data);
  private final UniversalDatagramPublisher universalDatagramPublisher = new UniversalDatagramPublisher(data, GROUP, PORT);
  //
  private final SpinnerLabel<Word> spinnerLabelLw = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelRw = new SpinnerLabel<>();
  private final SliderExt sliderExtLs;
  private final SliderExt sliderExtRs;

  public RimoPutComponent() {
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

  private TimerTask timerTask = null;

  @Override
  public void connectAction(int period, boolean isSelected) {
    if (isSelected) {
      timerTask = new TimerTask() {
        @Override
        public void run() {
          RimoPutEvent rimoPutEvent = new RimoPutEvent();
          rimoPutEvent.left_command = spinnerLabelLw.getValue().getShort();
          rimoPutEvent.left_speed = (short) sliderExtLs.jSlider.getValue();
          rimoPutEvent.right_command = spinnerLabelRw.getValue().getShort();
          rimoPutEvent.right_speed = (short) sliderExtRs.jSlider.getValue();
          byteBuffer.position(0);
          rimoPutEvent.insert(byteBuffer);
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
  public String connectionInfo() {
    return String.format("%s:%d", GROUP, PORT);
  }
}
