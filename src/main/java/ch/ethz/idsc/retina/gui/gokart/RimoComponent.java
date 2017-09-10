// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public class RimoComponent extends InterfaceComponent {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createShort("SET_SPEED", (short) 0xf234), //
      Word.createShort("SET_GEAR", (short) 0x4) //
  );

  public RimoComponent() {
    {
      JToolBar jToolBar = createRow("command word");
      SpinnerLabel<Word> spinnerLabel = new SpinnerLabel<>();
      spinnerLabel.setList(COMMANDS);
      spinnerLabel.setValueSafe(COMMANDS.get(0));
      spinnerLabel.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("command speed");
      // [rad/min]
      SliderExt sliderExt = SliderExt.wrap(new JSlider(-8000, 8000, 0));
      sliderExt.addToComponent(jToolBar);
    }
    {
      createRow(" ");
    }
    {
      JToolBar jToolBar = createRow("status word");
      JTextField jTextField = createReading();
      jToolBar.add(jTextField);
    }
    {
      JToolBar jToolBar = createRow("actual speed");
      JTextField jTextField = createReading();
      jToolBar.add(jTextField);
    }
    {
      JToolBar jToolBar = createRow("rms motor current");
      JTextField jTextField = createReading();
      jToolBar.add(jTextField);
    }
    {
      JToolBar jToolBar = createRow("dc bus voltage");
      JTextField jTextField = createReading();
      jToolBar.add(jTextField);
    }
  }
}
