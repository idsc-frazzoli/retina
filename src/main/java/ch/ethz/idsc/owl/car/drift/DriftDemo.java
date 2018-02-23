// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owl.math.region.NegativeHalfspaceRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

enum DriftDemo {
  ;
  public static void main(String[] args) {
    Collection<Flow> controls = DriftControls.create(10);
    Tensor eta = Tensors.vector(30, 30, 5);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, RationalScalar.of(1, 10), 7);
    System.out.println("scale=" + eta);
    GoalInterface goalInterface = DriftGoalManager.createStandard(//
        Tensors.vector(-0.3055, 0.5032, 8), //
        Tensors.vector(0.05, 0.05, 0.25));
    TrajectoryRegionQuery obstacleQuery = //
        SimpleTrajectoryRegionQuery.timeInvariant(new NegativeHalfspaceRegion(1));
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta, stateIntegrator, controls, obstacleQuery, goalInterface);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0, 0, 4), RealScalar.ZERO));
    int iters = Expand.maxSteps(trajectoryPlanner, 5000);
    System.out.println(iters);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<GlcNode> trajectory = Nodes.listFromRoot(optional.get());
      for (GlcNode node : trajectory) {
        if (!node.isRoot())
          System.out.println(node.flow().getU());
        System.out.println(node.stateTime().toInfoString());
      }
      // StateTimeTrajectories.print(trajectory);
    }
    OwlyGui.glc(trajectoryPlanner);
  }
}
