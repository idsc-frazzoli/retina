// code by jph
package ch.ethz.idsc.owl.car.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.model.CarState;
import ch.ethz.idsc.owl.car.model.CarStateSpaceModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.PlannerType;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class CarEntity extends TrajectoryEntity {
  private static final Integrator INTEGRATOR = RungeKutta4Integrator.INSTANCE;
  private static final Scalar MAX_TIME_STEP = RealScalar.of(.005);

  public CarEntity(CarStateSpaceModel carStateSpaceModel, CarState carState) {
    super(null, null); // FIXME
    // super(new BoundedEpisodeIntegrator( //
    // carStateSpaceModel, //
    // INTEGRATOR, //
    // new StateTime(carState.asVector(), RealScalar.ZERO), //
    // MAX_TIME_STEP));
  }

  @Override
  public PlannerType getPlannerType() {
    return PlannerType.STANDARD;
  }

  // @Override
  // protected Scalar distance(Tensor x, Tensor y) {
  // throw new RuntimeException();
  // }
  //
  // @Override
  // protected Tensor fallbackControl() {
  // return Array.zeros(5);
  // }
  @Override
  public Scalar delayHint() {
    throw new RuntimeException();
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(TrajectoryRegionQuery obstacleQuery, Tensor goal) {
    throw new RuntimeException();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor state = getStateTimeNow().state();
    CarState carState = new CarState(state);
    {
      Point2D point = geometricLayer.toPoint2D(carState.se2());
      graphics.setColor(new Color(64, 128, 64, 192));
      graphics.fill(new Ellipse2D.Double(point.getX() - 2, point.getY() - 2, 7, 7));
    }
    VehicleModel rimoSinusIonModel = RimoSinusIonModel.standard();
    {
      Color color = new Color(64, 64, 64, 128);
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(carState.se2()));
      graphics.setColor(color);
      graphics.fill(geometricLayer.toPath2D(rimoSinusIonModel.footprint()));
      geometricLayer.popMatrix();
    }
  }
}
