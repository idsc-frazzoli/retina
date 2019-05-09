// code by jph
package ch.ethz.idsc.owl.bot.se2.pid;

import java.io.IOException;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ConvergenceTest extends TestCase {
  /** A = Import["posepid.csv"];
   * ListPlot[A[[All, {1, 2}]], AspectRatio -> 1, PlotRange -> All] */
  public void testSimple() throws IOException {
    PIDGains pidGains = new PIDGains(Quantity.of(.4, "m^-2"), RealScalar.ZERO, Quantity.of(3, "s*m^-2"));
    PIDTrajectory pidTrajectory = null;
    // for (many different initial pose)
    TableBuilder tableBuilder = new TableBuilder();
    Tensor pose = Tensors.fromString("{0[m],2[m],0}");
    Tensor traj = Tensors.vector(i -> Tensors.of(Quantity.of(i / 10, SI.METER), Quantity.of(1, SI.METER), Pi.HALF), 2000);
    for (int index = 0; index < 100; ++index) {
      StateTime stateTime = new StateTime(pose, Quantity.of(index, SI.SECOND));
      PIDTrajectory _pidTrajectory = new PIDTrajectory(index, pidTrajectory, pidGains, traj, stateTime);
      pidTrajectory = _pidTrajectory;
      Scalar ratioOut = pidTrajectory.ratioOut();
      System.out.println("RatioOut: " + ratioOut);
      // clip within valid angle [-max, max]
      // Scalar maxTurningRate = Quantity.of(0.3, SI.PER_SECOND);
      // Clip turningRate = Clips.interval(maxTurningRate.negate(), maxTurningRate);
      // ratioOut = turningRate.apply(ratioOut);
      Tensor angleOut = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(ratioOut);
      Tensor vel = Tensors.of(Quantity.of(2, SI.VELOCITY), Quantity.of(0, SI.VELOCITY), angleOut);
      double dt = 0.1;
      pose = Se2CoveringIntegrator.INSTANCE. // Euler
          spin(pose, vel.multiply(Quantity.of(dt, SI.SECOND)));
      stateTime = new StateTime(pose, stateTime.time().add(Quantity.of(dt, SI.SECOND)));
      System.out.println(Pretty.of(pose));
      tableBuilder.appendRow(pose);
      // System.out.println(pidTrajectory.getProp());
      // System.out.println(pidTrajectory.getDeriv());
      // System.out.println("------------------_");
    }
    Export.of(HomeDirectory.file("posepid.csv"), tableBuilder.toTable());
  }
}
