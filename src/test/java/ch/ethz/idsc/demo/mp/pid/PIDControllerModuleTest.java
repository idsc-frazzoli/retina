// code by mcp
package ch.ethz.idsc.demo.mp.pid;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PIDControllerModuleTest extends TestCase {
  public void testFirstAlgoLast() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    pidControllerModule.first();
    pidControllerModule.runAlgo();
    pidControllerModule.last();
  }

  public void testHeading() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL_SE2.stream());
    pidControllerModule.setCurve(Optional.ofNullable(curve));
    pidControllerModule.first();
    Tensor pose = Tensors.fromString("{30[m],40[m], 1.57}");
    System.out.println(Pretty.of(curve));
    for (int index = 0; index < 100; index++) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvents.offlineV1(pose, RealScalar.ONE);
      pidControllerModule.getEvent(gokartPoseEvent);
      pidControllerModule.runAlgo();
      Scalar heading = pidControllerModule.pidSteer.getHeading();
      System.out.println("Heading: " + heading);
      //System.out.println("Error: " + pidControllerModule.getPID().getError().toString());
      pose = Se2CoveringIntegrator.INSTANCE.spin(pose, Tensors.of(Quantity.of(1, SI.METER), RealScalar.ZERO, heading));
      // System.out.println("Pose: " + pose);
      // TODO MCP Solve issue with if gokart does multiple rotations (+pi factor)
    }
  }

  public void testCurve() {
    Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL_SE2.stream().map(Extract2D.FUNCTION));
    for (int index = 0; index < curve.length(); index++) {
      // System.out.println(curve.get(index));
    }
    // System.out.println(curve.length());
  }

  public void testDistance() {
  }
}
