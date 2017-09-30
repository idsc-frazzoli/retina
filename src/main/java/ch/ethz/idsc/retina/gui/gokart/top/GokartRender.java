// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owly.gui.GeometricLayer;
import ch.ethz.idsc.owly.gui.RenderInterface;
import ch.ethz.idsc.owly.model.car.VehicleModel;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.tensor.Tensors;

public class GokartRender implements RenderInterface {
  private final VehicleModel vehicleModel;
  // ---
  private RimoGetEvent _rimoGetEvent;
  public final RimoGetListener rimoGetListener = getEvent -> _rimoGetEvent = getEvent;
  // ---
  private LinmotGetEvent _linmotGetEvent;
  public final LinmotGetListener linmotGetListener = getEvent -> _linmotGetEvent = getEvent;

  public GokartRender(VehicleModel vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(_rimoGetEvent)) {
      RimoGetEvent rimoGetEvent = _rimoGetEvent;
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(2).lever(), //
          Tensors.vector(rimoGetEvent.getL.getAngularRate().number(), 0)));
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(3).lever(), //
          Tensors.vector(rimoGetEvent.getR.getAngularRate().number(), 0)));
    }
    if (Objects.nonNull(_linmotGetEvent)) {
      LinmotGetEvent linmotGetEvent = _linmotGetEvent;
      // linmotGetEvent.
      // TODO draw brake
    }
  }
}
