//code by mcp
package ch.ethz.idsc.demo.mp;

import java.util.Optional;

import ch.ethz.idsc.demo.mp.pid.PIDController;
import ch.ethz.idsc.demo.mp.pid.PIDTuningParams;
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
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PIDControllerTest extends TestCase {
  public void testFirstAlgoLast() {
    PIDController pidController = new PIDController(PIDTuningParams.GLOBAL);
    pidController.first();
    pidController.runAlgo();
    pidController.last();
  }

  public void testHeading() {
    PIDController pidController = new PIDController(PIDTuningParams.GLOBAL);
    Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL.stream().map(Extract2D.FUNCTION));
    pidController.setCurve(Optional.ofNullable(curve));
    System.out.println(curve);
    pidController.first();
    Tensor pose = Tensors.fromString("{40[m], 30[m], 1}");
    for (int index = 0; index < 100; index++) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvents.create(pose, RealScalar.ONE);
      pidController.getEvent(gokartPoseEvent);
      pidController.runAlgo();
      Scalar heading = pidController.pidSteer.getHeading();
      // System.out.println(heading);
      pose = Se2CoveringIntegrator.INSTANCE.spin(pose, Tensors.of(Quantity.of(1, SI.METER), RealScalar.ZERO, heading.divide(RealScalar.of(10))));
      System.out.println(pose);
      
      // TODO Solve issue with if gokart does multiple rotations (+pi factor)
    }
  }

  public void testCurve() { // Not going trough this if function not starting with "test-"
    Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL.stream().map(Extract2D.FUNCTION));
    for (int index = 0; index < curve.length(); index++) {
      // System.out.println(curve.get(index));
    }
    // System.out.println(curve.length());
  }
}
