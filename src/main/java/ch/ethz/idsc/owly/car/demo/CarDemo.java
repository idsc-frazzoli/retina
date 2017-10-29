// code by jph
package ch.ethz.idsc.owly.car.demo;

import ch.ethz.idsc.owly.car.core.VehicleModel;
import ch.ethz.idsc.owly.car.model.CarState;
import ch.ethz.idsc.owly.car.model.CarStateSpaceModel;
import ch.ethz.idsc.owly.car.model.CarStatic;
import ch.ethz.idsc.owly.car.model.HomogenousTrack;
import ch.ethz.idsc.owly.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owly.demo.rn.R2NoiseRegion;
import ch.ethz.idsc.owly.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owly.gui.ani.OwlyAnimationFrame;
import ch.ethz.idsc.owly.math.region.Region;
import ch.ethz.idsc.tensor.RealScalar;

public class CarDemo {
  // @Override
  public void start() {
    VehicleModel vehicleModel = RimoSinusIonModel.standard();
    CarStateSpaceModel carStateSpaceModel = new CarStateSpaceModel(vehicleModel, HomogenousTrack.DRY_ROAD);
    CarState carState = CarStatic.x0_demo1(vehicleModel);
    CarEntity carEntity = new CarEntity(carStateSpaceModel, carState);
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.set(carEntity);
    Region region = new R2NoiseRegion(RealScalar.of(0.2));
    owlyAnimationFrame.setObstacleQuery(SimpleTrajectoryRegionQuery.timeInvariant(region));
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new CarDemo().start();
  }
}
