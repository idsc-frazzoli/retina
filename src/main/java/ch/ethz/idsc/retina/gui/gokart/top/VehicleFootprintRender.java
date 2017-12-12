// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owly.car.core.VehicleModel;
import ch.ethz.idsc.owly.car.core.WheelInterface;
import ch.ethz.idsc.tensor.Tensor;

class VehicleFootprintRender implements RenderInterface {
  private final VehicleModel vehicleModel;

  public VehicleFootprintRender(VehicleModel vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    {
      graphics.setColor(new Color(192, 192, 192, 64));
      graphics.fill(geometricLayer.toPath2D(vehicleModel.footprint()));
    }
    {
      graphics.setColor(Color.RED);
      int wheels = vehicleModel.wheels();
      for (int index = 0; index < wheels; ++index) {
        WheelInterface wheelInterface = vehicleModel.wheel(index);
        Tensor pos = wheelInterface.lever();
        Point2D point2D = geometricLayer.toPoint2D(pos);
        graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 2, 2);
      }
    }
  }
}
