// code by mcp
package ch.ethz.idsc.demo.mp.pid;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringIntegrator;
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

  public void testSetCurve() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    pidControllerModule.setCurve(Optional.ofNullable(CURVE));
    assertTrue(pidControllerModule.getCurve().isPresent());
  }

  public void testHeadingError() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    pidControllerModule.setCurve(Optional.ofNullable(CURVE));
    pidControllerModule.first();
    Tensor pose = Tensors.fromString("{30[m], 40[m], 1.57}");
    for (int index = 0; index < 100; index++) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvents.offlineV1(pose, RealScalar.ONE);
      pidControllerModule.getEvent(gokartPoseEvent);
      pidControllerModule.runAlgo();
      Scalar ratio = pidControllerModule.pidSteer.getRatio();
      double dt = 0.1;
      Scalar vx = Quantity.of(1, SI.VELOCITY);
      Tensor u = Tensors.of(vx, vx.zero(), ratio.multiply(vx));
      pose = Se2CoveringIntegrator.INSTANCE. // Euler
          spin(pose, u.multiply(Quantity.of(dt, SI.SECOND)));
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        System.out.println("Turning ratio: " + ratio + "   ");
      }
    }
  }

  public void testPoseError() {
    PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
    pidControllerModule.setCurve(Optional.ofNullable(CURVE));
    pidControllerModule.first();
    Tensor pose = Tensors.fromString("{30[m], 40[m], 1.57}");
    for (int index = 0; index < 100; index++) {
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        // System.out.println("----------------------Interation" + index);
      }
      GokartPoseEvent gokartPoseEvent = GokartPoseEvents.offlineV1(pose, RealScalar.ONE);
      pidControllerModule.getEvent(gokartPoseEvent);
      pidControllerModule.runAlgo();
      Scalar ratio = pidControllerModule.pidSteer.getRatio();
      if (UserName.is("maximilien") || UserName.is("datahaki")) {
        // System.out.println("Ratio out: " + ratio);
        // System.out.println("Pose: " + Pretty.of(pose));
        System.out.println("PIDerror: " + pidControllerModule.getPID().getError());
      }
      double dt = 0.1;
      Scalar vx = Quantity.of(1, SI.VELOCITY);
      Tensor u = Tensors.of(vx, vx.zero(), ratio.multiply(vx));
      pose = Se2CoveringIntegrator.INSTANCE. // Euler
          spin(pose, u.multiply(Quantity.of(dt, SI.SECOND)));
    }
  }

  public void testDistance() {
    // TODO MCP
  }

  public void testUnits() {
    // TODO MCP
  }
}
