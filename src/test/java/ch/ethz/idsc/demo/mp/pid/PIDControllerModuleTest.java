// code by mcp
package ch.ethz.idsc.demo.mp.pid;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PIDControllerModuleTest extends TestCase {
  private static final Tensor CURVE = DubendorfCurve.TRACK_OVAL_SE2;

  public void testFirstAlgoLast() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    pidControllerModule.first();
    pidControllerModule.runAlgo();
    pidControllerModule.last();
  }

  public void testHeadingError() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    pidControllerModule.setCurve(Optional.ofNullable(CURVE));
    pidControllerModule.first();
    Tensor pose = Tensors.fromString("{30[m],40[m], 1.57}");
    // FIXME MCP increase limit from 1 to 100
    for (int index = 0; index < 1; index++) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvents.offlineV1(pose, RealScalar.ONE);
      pidControllerModule.getEvent(gokartPoseEvent);
      pidControllerModule.runAlgo();
      Scalar ratio = pidControllerModule.pidSteer.getRatio(); // TODO mcp fix
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println("Heading: " + ratio);
        System.out.println("Error: " + pidControllerModule.getPID().getError());
      }
      Scalar vx = Quantity.of(1, SI.VELOCITY);
      Tensor u = Tensors.of(vx, vx.zero(), vx.multiply(ratio));
      pose = Se2CoveringIntegrator.INSTANCE.spin(pose, u.multiply(Quantity.of(0.1, SI.SECOND)));
      // TODO MCP Solve issue with if gokart does multiple rotations (+pi factor)
    }
  }

  public void testPoseError() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    pidControllerModule.setCurve(Optional.ofNullable(CURVE));
    pidControllerModule.first();
    Tensor pose = Tensors.fromString("{30[m],40[m], 1.57}");
    // FIXME MCP increase limit from 1 to 100
    for (int index = 0; index < 1; index++) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvents.offlineV1(pose, RealScalar.ONE);
      pidControllerModule.getEvent(gokartPoseEvent);
      pidControllerModule.runAlgo();
      Scalar ratio = pidControllerModule.pidSteer.getRatio(); // TODO mcp fix
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        // System.out.println("Error: " + pidControllerModule.getPID().getError().toString());
        // System.out.println("Pose: " + Pretty.of(pose));
      }
      Scalar vx = Quantity.of(1, SI.VELOCITY);
      Tensor u = Tensors.of(vx, vx.zero(), vx.multiply(ratio));
      pose = Se2CoveringIntegrator.INSTANCE.spin(pose, u.multiply(Quantity.of(0.1, SI.SECOND)));
    }
  }

  public void testDistance() {
    // TODO MCP
  }

  public void testUnits() {
    // TODO MCP
  }
}
