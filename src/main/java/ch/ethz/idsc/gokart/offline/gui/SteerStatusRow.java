// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.qty.Boole;

/* package */ class SteerStatusRow extends MappedLogImageRow implements SteerGetListener {
  private Scalar scalar = RealScalar.ZERO;

  @Override // from SteerGetListener
  public void getEvent(SteerGetEvent steerGetEvent) {
    scalar = Boole.of(steerGetEvent.isActive());
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    return scalar;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.COPPER;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "steer status";
  }

  @Override // from DiscreteLogImageRow
  public Map<Scalar, String> legend() {
    LinkedHashMap<Scalar, String> linkedHashMap = new LinkedHashMap<>();
    linkedHashMap.put(Boole.of(false), "passive");
    linkedHashMap.put(Boole.of(true), "active");
    return linkedHashMap;
  }
}
