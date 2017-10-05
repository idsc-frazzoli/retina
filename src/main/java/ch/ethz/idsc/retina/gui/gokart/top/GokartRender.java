// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owly.gui.GeometricLayer;
import ch.ethz.idsc.owly.gui.RenderInterface;
import ch.ethz.idsc.owly.math.car.SteeringWheelAngle;
import ch.ethz.idsc.owly.model.car.VehicleModel;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.AngleVector;

public class GokartRender implements RenderInterface {
  private final VehicleModel vehicleModel;
  // ---
  private RimoGetEvent rimoGetEvent;
  public final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  // ---
  private LinmotGetEvent linmotGetEvent;
  public final LinmotGetListener linmotGetListener = getEvent -> linmotGetEvent = getEvent;
  // ---
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;

  public GokartRender(VehicleModel vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // rear wheels
    if (Objects.nonNull(rimoGetEvent)) {
      graphics.setStroke(new BasicStroke(2));
      graphics.setColor(Color.GREEN);
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(2).lever(), //
          Tensors.vector(rimoGetEvent.getL.getAngularRate().number().doubleValue() * 1e-2, 0)));
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(3).lever(), //
          Tensors.vector(rimoGetEvent.getR.getAngularRate().number().doubleValue() * 1e-2, 0)));
    }
    if (Objects.nonNull(linmotGetEvent)) {
      Tensor brakePosition = Tensors.vector(1.0, 0.05);
      graphics.setStroke(new BasicStroke(2));
      graphics.setColor(Color.BLACK);
      graphics.draw(geometricLayer.toVector( //
          brakePosition, //
          Tensors.vector(linmotGetEvent.getActualPosition().number().doubleValue() * -10, 0)));
    }
    if (Objects.nonNull(gokartStatusEvent)) {
      Scalar angle = gokartStatusEvent.getSteeringAngle();
      Scalar angleL = SteeringWheelAngle.of(RealScalar.of(-48.0 / 118.0), angle);
      Scalar angleR = SteeringWheelAngle.of(RealScalar.of(+48.0 / 118.0), angle);
      graphics.setStroke(new BasicStroke(2));
      graphics.setColor(Color.GRAY);
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(0).lever(), //
          AngleVector.of(angleL).multiply(RealScalar.of(0.3))));
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(1).lever(), //
          AngleVector.of(angleR).multiply(RealScalar.of(0.3))));
    }
    graphics.setStroke(new BasicStroke());
  }
}
