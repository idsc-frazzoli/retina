// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

//TODO mcp implementation does not use I-part
public class PIDTrajectory {
  private final Scalar time;
  private final Scalar errorPose;
  private Scalar ratioOut;
  private Scalar deriv = RealScalar.ZERO;
  private Scalar prop;
  private Tensor closest;

  public PIDTrajectory(int pidIndex, PIDTrajectory previousPID, PIDGains pidGains, Tensor traj, StateTime stateTime) {
    this.time = stateTime.time();
    Tensor stateXYphi = stateTime.state();
    TensorUnaryOperator tuo = new Se2GroupElement(stateXYphi).inverse()::combine;
    Tensor curveLocally = Tensor.of(traj.stream().map(tuo));
    // --
    // Distance metric
    closest = curveLocally.get(Se2CurveHelper.closestEuclid(curveLocally));
    // TODO MCP unfortunately Se2CoveringParametricDistance ignores heading if xy are correct
    // Idea: ClothoidCurve
    // --
    this.errorPose = closest.Get(1);
    prop = pidGains.Kp.multiply(errorPose);
    if (pidIndex > 1) {
      Scalar dt = time.subtract(previousPID.time);
      deriv = pidGains.Kd.multiply((errorPose.subtract(previousPID.errorPose)).divide(dt));
    }
    ratioOut = prop.add(deriv);
    // ratioOut = RnUnitCircle.convert(ratioOut); //TODO mcp Check if need to add
  }

  public Scalar ratioOut() {
    return ratioOut;
  }

  public Scalar getProp() {
    return prop;
  }

  public Scalar getDeriv() {
    return deriv;
  }

  public Scalar getError() { // for debug
    return errorPose;
  }
}
