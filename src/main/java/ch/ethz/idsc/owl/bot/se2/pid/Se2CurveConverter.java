// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum Se2CurveConverter implements Se2UnitConverter {
  INSTANCE;
  /** @param unitless traj
   * @return traj with unit {x[m], y[m], phi} */
  @Override
  public Tensor toSI(Tensor traj) {
    return Tensor.of(traj.stream().map(PoseHelper::attachUnits));
  }
}
