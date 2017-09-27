package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.tensor.img.ColorDataFunction;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.io.ResourceData;

public enum Gui {
  INSTANCE;
  public final ColorDataFunction TEMPERATURE;
  public final ColorDataFunction TEMPERATURE_LIGHT;

  private Gui() {
    TEMPERATURE = ColorDataGradient.of(ResourceData.of("/colorscheme/temperature.csv"));
    TEMPERATURE_LIGHT = ColorDataGradient.of(ResourceData.of("/colorscheme/temperature_light.csv"));
  }
}
