// code by jph
package ch.ethz.idsc.owl.car.demo;

import ch.ethz.idsc.owl.bot.r2.R2NoiseRegion;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.model.CarState;
import ch.ethz.idsc.owl.car.model.CarStateSpaceModel;
import ch.ethz.idsc.owl.car.model.CarStatic;
import ch.ethz.idsc.owl.car.model.HomogenousTrack;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public class CarDemo {
  // @Override
  public void start() {
    VehicleModel vehicleModel = RimoSinusIonModel.standard();
    CarStateSpaceModel carStateSpaceModel = new CarStateSpaceModel(vehicleModel, HomogenousTrack.DRY_ROAD);
    CarState carState = CarStatic.x0_demo1(vehicleModel);
    CarEntity carEntity = new CarEntity(carStateSpaceModel, carState);
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.set(carEntity);
    Region<Tensor> region = new R2NoiseRegion(RealScalar.of(0.2));
    owlyAnimationFrame.setObstacleQuery(SimpleTrajectoryRegionQuery.timeInvariant(region));
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new CarDemo().start();
  }
}
