// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

import javax.swing.JSlider;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvents;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutPublisher;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public class LinmotPutComponent extends InterfaceComponent {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createShort("CONFIG", (short) 0xf234), //
      Word.createShort("SET_POS", (short) 0x7) //
  );
  public static final List<Word> HEADER = Arrays.asList( //
      Word.createShort("SOME1", (short) 0xfedc), //
      Word.createShort("SOME2", (short) 0x9876) //
  );
  public static final int PORT = 5000;
  // public static final String GROUP = "225.4.5.6";
  public static final String GROUP = "localhost";
  // public static final String GROUP = "239.255.76.67";
  // ---
  private final LinmotPutPublisher linmotPutPublisher = new LinmotPutPublisher(PORT, GROUP);
  //
  private final SpinnerLabel<Word> spinnerLabelF0;
  private final SpinnerLabel<Word> spinnerLabelF1;
  private final SliderExt sliderExtF2;
  private final SliderExt sliderExtF3;
  private final SliderExt sliderExtF4;
  private final SliderExt sliderExtF5;

  public LinmotPutComponent() {
    final LinmotPutEvent init = LinmotPutEvents.createInitial();
    {
      JToolBar jToolBar = createRow("control word");
      spinnerLabelF0 = new SpinnerLabel<>();
      spinnerLabelF0.setList(COMMANDS);
      spinnerLabelF0.setValueSafe(COMMANDS.get(0));
      spinnerLabelF0.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("motion cmd hdr");
      spinnerLabelF1 = new SpinnerLabel<>();
      spinnerLabelF1.setList(HEADER);
      spinnerLabelF1.setValueSafe(HEADER.get(0));
      spinnerLabelF1.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // target pos
      JToolBar jToolBar = createRow("target pos");
      sliderExtF2 = SliderExt.wrap(new JSlider(Short.MIN_VALUE, Short.MAX_VALUE, 0));
      sliderExtF2.addToComponent(jToolBar);
      sliderExtF2.setValueShort(init.target_position);
    }
    { // max velocity
      JToolBar jToolBar = createRow("velocity max");
      sliderExtF3 = SliderExt.wrap(new JSlider(0, MAX_USHORT, init.max_velocity & 0xffff));
      sliderExtF3.addToComponent(jToolBar);
    }
    { // acceleration
      JToolBar jToolBar = createRow("acceleration");
      sliderExtF4 = SliderExt.wrap(new JSlider(0, MAX_USHORT, init.acceleration & 0xffff));
      sliderExtF4.addToComponent(jToolBar);
    }
    { // deceleration
      JToolBar jToolBar = createRow("deceleration");
      sliderExtF5 = SliderExt.wrap(new JSlider(0, MAX_USHORT, init.deceleration & 0xffff));
      sliderExtF5.addToComponent(jToolBar);
    }
  }

  private TimerTask timerTask = null;

  @Override
  public void connectAction(int period, boolean isSelected) {
    if (isSelected) {
      timerTask = new TimerTask() {
        @Override
        public void run() {
          LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
          linmotPutEvent.control_word = spinnerLabelF0.getValue().getShort();
          linmotPutEvent.motion_cmd_hdr = spinnerLabelF1.getValue().getShort();
          linmotPutEvent.target_position = (short) sliderExtF2.jSlider.getValue();
          linmotPutEvent.max_velocity = (short) sliderExtF3.jSlider.getValue();
          linmotPutEvent.acceleration = (short) sliderExtF4.jSlider.getValue();
          linmotPutEvent.deceleration = (short) sliderExtF5.jSlider.getValue();
          System.out.println("linmot put");
          System.out.println(linmotPutEvent.toInfoString());
          linmotPutEvent.insert(linmotPutPublisher.byteBuffer());
          linmotPutPublisher.send();
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
