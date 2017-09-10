// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

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
      sliderExt.physics = x -> {
        return Quantity.of((Scalar) x.divide(RealScalar.of(60.0)).map(Round._3), "[rad*s^-1]");
      };
      sliderExt.addToComponent(jToolBar);
    }
    {
      createRow(" ");
    }
    {
      // JToolBar jToolBar = createRow();
      JTextField jTextField = createReading("status word");
    }
    {
      // JToolBar jToolBar = createRow();
      JTextField jTextField = createReading("actual speed");
    }
    {
      // JToolBar jToolBar = createRow();
      JTextField jTextField = createReading("rms motor current");
      // jToolBar.add(jTextField);
    }
    {
      // JToolBar jToolBar = createRow();
      JTextField jTextField = createReading("dc bus voltage");
      // jToolBar.add(jTextField);
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
