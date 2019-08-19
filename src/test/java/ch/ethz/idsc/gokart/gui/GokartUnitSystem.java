// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.HashMap;
import java.util.Map;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.SimpleUnitSystem;
import ch.ethz.idsc.tensor.qty.UnitSystem;

public enum GokartUnitSystem {
  INSTANCE;
  // ---
  public final UnitSystem unitSystem;

  private GokartUnitSystem() {
    Map<String, Scalar> map = new HashMap<>(UnitSystem.SI().map());
    // introduce units similar to Kelvin: K=1[K]
    // units of steering unit
    map.put("SCE", Quantity.of(1, "SCE"));
    map.put("SCT", Quantity.of(1, "SCT"));
    // units of motor
    map.put("ARMS", Quantity.of(1, "ARMS"));
    // units of brake
    map.put("degC", Quantity.of(1, "degC"));
    unitSystem = SimpleUnitSystem.from(map);
  }
}
