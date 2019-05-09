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
  private Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL_SE2.stream());

  public void testFirstAlgoLast() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    pidControllerModule.first();
    pidControllerModule.runAlgo();
    pidControllerModule.last();
  }

  public void testHeadingError() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    pidControllerModule.setCurve(Optional.ofNullable(curve));
    pidControllerModule.first();
    Tensor pose = Tensors.fromString("{30[m],40[m], 1.57}");
    for (int index = 0; index < 100; index++) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvents.offlineV1(pose, RealScalar.ONE);
      pidControllerModule.getEvent(gokartPoseEvent);
      pidControllerModule.runAlgo();
      Scalar ratio = pidControllerModule.pidSteer.getRatio(); // TODO mcp fix
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println("Heading: " + ratio);
        System.out.println("Error: " + pidControllerModule.getPID().getError().toString());
      }
      pose = Se2CoveringIntegrator.INSTANCE.spin(pose, Tensors.of(Quantity.of(1, SI.METER), RealScalar.ZERO, ratio));
      // TODO MCP Solve issue with if gokart does multiple rotations (+pi factor)
    }
  }

  public void testPoseError() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    pidControllerModule.setCurve(Optional.ofNullable(curve));
    pidControllerModule.first();
    Tensor pose = Tensors.fromString("{30[m],40[m], 1.57}");
    for (int index = 0; index < 100; index++) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvents.offlineV1(pose, RealScalar.ONE);
      pidControllerModule.getEvent(gokartPoseEvent);
      pidControllerModule.runAlgo();
      Scalar ratio = pidControllerModule.pidSteer.getRatio(); // TODO mcp fix
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        // System.out.println("Error: " + pidControllerModule.getPID().getError().toString());
        // System.out.println("Pose: " + Pretty.of(pose));
      }
      pose = Se2CoveringIntegrator.INSTANCE.spin(pose, Tensors.of(Quantity.of(1, SI.METER), RealScalar.ZERO, ratio));
    }
  }

  public void testDistance() {
    // TODO MCP
  }

  public void testUnits() {
    // TODO MCP
  }
}
