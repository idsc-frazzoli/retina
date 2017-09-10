// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public class LinmotPutComponent extends InterfaceComponent {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createShort("CONFIG", (short) 0xf234), //
      Word.createShort("SET_POS", (short) 0x4) //
  );
  public static final List<Word> HEADER = Arrays.asList( //
      Word.createShort("SOME1", (short) 0xf234), //
      Word.createShort("SOME2", (short) 0x4) //
  );

  public LinmotPutComponent() {
    {
      JToolBar jToolBar = createRow("control word");
      SpinnerLabel<Word> spinnerLabel = new SpinnerLabel<>();
      spinnerLabel.setList(COMMANDS);
      spinnerLabel.setValueSafe(COMMANDS.get(0));
      spinnerLabel.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("motion cmd hdr");
      SpinnerLabel<Word> spinnerLabel = new SpinnerLabel<>();
      spinnerLabel.setList(HEADER);
      spinnerLabel.setValueSafe(HEADER.get(0));
      spinnerLabel.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // target pos
      JToolBar jToolBar = createRow("target pos");
      SliderExt sliderExt = SliderExt.wrap( //
          new JSlider(Short.MIN_VALUE, Short.MAX_VALUE, 0));
      sliderExt.addToComponent(jToolBar);
    }
    { // max velocity
      JToolBar jToolBar = createRow("velocity max");
      SliderExt sliderExt = SliderExt.wrap(new JSlider(0, MAX_USHORT, 4096));
      sliderExt.addToComponent(jToolBar);
    }
    { // acceleration
      JToolBar jToolBar = createRow("acceleration");
      SliderExt sliderExt = SliderExt.wrap(new JSlider(0, MAX_USHORT, 2000));
      sliderExt.addToComponent(jToolBar);
    }
    { // deceleration
      JToolBar jToolBar = createRow("deceleration");
      SliderExt sliderExt = SliderExt.wrap(new JSlider(0, MAX_USHORT, 2000));
      sliderExt.addToComponent(jToolBar);
    }
  }

  @Override
  public void connectAction(boolean isSelected) {
    System.err.println("not implemented");
  }

  @Override
  public String connectionInfo() {
    return "n.a.";
  }
}
