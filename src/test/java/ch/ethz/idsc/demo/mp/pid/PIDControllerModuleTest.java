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
import ch.ethz.idsc.tensor.io.Pretty;
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
      Scalar ratio = pidControllerModule.pidSteer.getRatio();
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        // System.out.println("Heading: " + Pretty.of(ratio));
        // System.out.println("Error: " + pidControllerModule.getPID().getError()); FIXME MCP
      }
      pose = Se2CoveringIntegrator.INSTANCE.spin(pose, Tensors.of(Quantity.of(1, SI.METER), RealScalar.ZERO, ratio));
      // Heading: 0.0[m^-1]
      // FIXME MCP bug in heading
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
      Scalar ratio = pidControllerModule.pidSteer.getRatio();
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println("--------------------- new iter -------------------------");
        System.out.println("Ratio out: " + ratio);
        System.out.println("Pose: " + Pretty.of(pose));
        System.out.println("PID: " + pidControllerModule.getPID());
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
