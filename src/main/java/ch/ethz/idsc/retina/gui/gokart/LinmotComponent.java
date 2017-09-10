// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.JSlider;
import javax.swing.JToolBar;

public class LinmotComponent extends InterfaceComponent {
  public LinmotComponent() {
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
}
