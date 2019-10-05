// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.qty.Boole;

/* package */ class LinmotOperationalRow extends MappedLogImageRow implements LinmotGetListener {
  private Scalar scalar = RealScalar.ZERO;

  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    scalar = Boole.of(linmotGetEvent.isOperational());
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    return scalar;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.AURORA;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "linmot operational";
  }

  @Override // from DiscreteLogImageRow
  public Map<Scalar, String> legend() {
    LinkedHashMap<Scalar, String> linkedHashMap = new LinkedHashMap<>();
    linkedHashMap.put(Boole.of(false), "passive");
    linkedHashMap.put(Boole.of(true), "active");
    return linkedHashMap;
  }
}
