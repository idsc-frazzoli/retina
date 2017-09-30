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
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;
import ch.ethz.idsc.tensor.Tensors;

public class GokartRender implements RenderInterface {
  private final VehicleModel vehicleModel;
  // ---
  private RimoGetEvent rimoGetEvent;
  public final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  // ---
  private LinmotGetEvent linmotGetEvent;
  public final LinmotGetListener linmotGetListener = getEvent -> linmotGetEvent = getEvent;
  // ---
  private SteerGetEvent steerGetEvent;
  public final SteerGetListener steerGetListener = getEvent -> steerGetEvent = getEvent;

  public GokartRender(VehicleModel vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // rear wheels
    if (Objects.nonNull(rimoGetEvent)) {
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(2).lever(), //
          Tensors.vector(rimoGetEvent.getL.getAngularRate().number(), 0)));
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(3).lever(), //
          Tensors.vector(rimoGetEvent.getR.getAngularRate().number(), 0)));
    }
    if (Objects.nonNull(linmotGetEvent)) {
      // linmotGetEvent.
      // TODO draw brake
    }
    if (Objects.nonNull(steerGetEvent)) {
      double angle = steerGetEvent.getGcpRelRckPos();
      // TODO draw brake
    }
  }
}
