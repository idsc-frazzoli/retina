// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owl.math.region.HyperplaneRegion;
import ch.ethz.idsc.owl.math.region.NegativeHalfspaceRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

enum DriftExtDemo {
  ;
  public static void main(String[] args) throws IOException {
    // the resolution refers to the last 3 of the state coordinates (x,y,theta,beta,r,Ux)
    Tensor eta = Tensors.vector(30, 30, 5); // magic const
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, RationalScalar.of(1, 10), 7);
    System.out.println("scale=" + eta);
    Collection<Flow> controls = new DriftExtFlows().getFlows(10); // magic const
    GoalInterface goalInterface = DriftGoalManager.createStandard(//
        Tensors.vector(0, 0, 0, -0.3055, 0.5032, 8), //
        Tensors.vector( //
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, //
            0.05, 0.05, 0.25));
    // ---
    Region<Tensor> region = RegionUnion.wrap(Arrays.asList( //
        new NegativeHalfspaceRegion(4) // ensure that r is non-negative
        , // impose that x < Threshold
        new HyperplaneRegion(Tensors.vector(-1, 0, 0, 0, 0, 0), RealScalar.of(12)) //
    ));
    // ---
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    // consider only (beta,r,Ux)
    StateTimeRaster stateTimeRaster = new EtaRaster(eta, x -> x.state().extract(3, 6));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, plannerConstraint, goalInterface);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0, 0, 0, 0, 0, 1), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(10000);
    int iters = glcExpand.getExpandCount();
    // Expand.maxSteps(trajectoryPlanner, 10000);
    System.out.println(iters);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<GlcNode> trajectory = Nodes.listFromRoot(optional.get());
      GlcNodeExport glcNodeExport = new GlcNodeExport("t, x, y, theta, beta, r, Ux, delta, Fx");
      for (GlcNode node : trajectory) {
        if (!node.isRoot())
          System.out.println(node.flow().getU());
        System.out.println(node.stateTime().toInfoString());
        glcNodeExport.append(node);
      }
      // StateTimeTrajectories.print(trajectory);
      new File("export").mkdir();
      glcNodeExport.writeToFile(new File("export/drift.csv"));
    }
    OwlyGui.glc(trajectoryPlanner);
  }
}
