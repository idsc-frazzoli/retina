// code by jph, mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import java.io.IOException;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class PIDConvergenceTest extends TestCase {
  /** PID Gains found by try and error */
  private PIDGains pidGains = new PIDGains( //
      Quantity.of(.7, "m^-2"), //
      RealScalar.ZERO, //
      Quantity.of(5, "s*m^-2"));
  private TableBuilder tableBuilder = new TableBuilder();
  Tensor pose = Tensors.fromString("{0[m],2[m],1.57}");

  /** Mathematica plots
   * A = Import["posepid.csv"];
   * ListPlot[A[[All, {1, 2}]], AspectRatio -> 1, PlotRange -> All] */
  public void testSimplePlotter() throws IOException {
    PIDTrajectory pidTrajectory = null;
    Tensor traj = Tensors.vector(i -> Tensors.of(Quantity.of(i, SI.METER), Quantity.of(1, SI.METER), Pi.HALF), 2000);
    for (int index = 0; index < 100; ++index) {
      StateTime stateTime = new StateTime(pose, Quantity.of(index, SI.SECOND));
      PIDTrajectory _pidTrajectory = new PIDTrajectory(index, pidTrajectory, pidGains, traj, stateTime);
      pidTrajectory = _pidTrajectory;
      Scalar ratioOut = pidTrajectory.ratioOut();
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println("RatioOut: " + ratioOut);
      }
      Scalar speed = Quantity.of(2, SI.VELOCITY);
      Tensor vel = Tensors.of(speed, speed.zero(), ratioOut.multiply(speed));
      double dt = 0.1;
      pose = Se2CoveringIntegrator.INSTANCE. // Euler
          spin(pose, vel.multiply(Quantity.of(dt, SI.SECOND)));
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println(Pretty.of(vel.multiply(Quantity.of(dt, SI.SECOND))));
      }
      stateTime = new StateTime(pose, stateTime.time().add(Quantity.of(dt, SI.SECOND)));
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println(Pretty.of(pose));
        tableBuilder.appendRow(PoseHelper.toUnitless(pose));
      }
    }
    if (UserName.is("maximilien") || UserName.is("datahaki")) {
      Export.of(HomeDirectory.file("posepid.csv"), tableBuilder.toTable());
    }
  }

  public void testRatioConvergenceMultiplePoseY() {
    Scalar ratioOut = RealScalar.ZERO;
    PIDTrajectory pidTrajectory = null;
    for (int poseId = -10; poseId <= 10; ++poseId) {
      Tensor pose = Tensors.of(Quantity.of(0, SI.METER), Quantity.of(poseId, SI.METER), Pi.HALF);
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println(" Pose: " + Pretty.of(pose));
      }
      Tensor traj = Tensors.vector(i -> Tensors.of(Quantity.of(i, SI.METER), Quantity.of(1, SI.METER), Pi.HALF), 2000);
      for (int index = 0; index < 100; ++index) {
        StateTime stateTime = new StateTime(pose, Quantity.of(index, SI.SECOND));
        PIDTrajectory _pidTrajectory = new PIDTrajectory(index, pidTrajectory, pidGains, traj, stateTime);
        pidTrajectory = _pidTrajectory;
        ratioOut = pidTrajectory.ratioOut();
        Scalar speed = Quantity.of(2, SI.VELOCITY);
        Tensor vel = Tensors.of(speed, speed.zero(), ratioOut.multiply(speed));
        double dt = 0.1;
        pose = Se2CoveringIntegrator.INSTANCE. // Euler
            spin(pose, vel.multiply(Quantity.of(dt, SI.SECOND)));
        stateTime = new StateTime(pose, stateTime.time().add(Quantity.of(dt, SI.SECOND)));
        Chop._03.requireClose(RealScalar.ZERO, ratioOut);
      }
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println("RatioOut: " + ratioOut);
      }
    }
  }

  public void testRatioConvergenceMultiplePoseAngle() {
    Scalar ratioOut = RealScalar.ZERO;
    PIDTrajectory pidTrajectory = null;
    double size = 5;
    for (int poseId = 0; poseId <= size; ++poseId) {
      double factor = poseId / size;
      Tensor pose = Tensors.of( //
          Quantity.of(0, SI.METER), //
          Quantity.of(2, SI.METER), //
          Pi.VALUE.multiply(RealScalar.of(factor)));
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println("PoseId: " + poseId + " Pose: " + Pretty.of(pose));
      }
      Tensor traj = Tensors.vector(i -> Tensors.of(Quantity.of(i, SI.METER), Quantity.of(1, SI.METER), Pi.HALF), 2000);
      for (int index = 0; index < 100; ++index) {
        StateTime stateTime = new StateTime(pose, Quantity.of(index, SI.SECOND));
        PIDTrajectory _pidTrajectory = new PIDTrajectory(index, pidTrajectory, pidGains, traj, stateTime);
        pidTrajectory = _pidTrajectory;
        ratioOut = pidTrajectory.ratioOut();
        Scalar speed = Quantity.of(2, SI.VELOCITY);
        Tensor vel = Tensors.of(speed, speed.zero(), ratioOut.multiply(speed));
        double dt = 0.1;
        pose = Se2CoveringIntegrator.INSTANCE. // Euler
            spin(pose, vel.multiply(Quantity.of(dt, SI.SECOND)));
        stateTime = new StateTime(pose, stateTime.time().add(Quantity.of(dt, SI.SECOND)));
      }
      Chop._03.requireClose(RealScalar.ZERO, ratioOut);
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println("RatioOut: " + ratioOut);
      }
    }
  }
}
