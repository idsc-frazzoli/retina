// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
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
import junit.framework.TestCase;

public class DriftExtTest extends TestCase {
  public void testSimple() {
    // the resolution refers to the last 3 of the state coordinates (x,y,theta,beta,r,Ux)
    Tensor eta = Tensors.vector(30, 30, 5);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, RationalScalar.of(1, 10), 7);
    Collection<Flow> controls = new DriftExtFlows().getFlows(10);
    GoalInterface goalInterface = DriftGoalManager.createStandard(//
        Tensors.vector(0, 0, 0, -0.3055, 0.5032, 8), //
        Tensors.vector( //
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, //
            0.05, 0.05, 0.25));
    // ---
    Region<Tensor> region = RegionUnion.wrap(Arrays.asList( //
        new NegativeHalfspaceRegion(4) // ensure that r is non-negative
        , // impose that x < Threshold
        new HyperplaneRegion(Tensors.vector(-1, 0, 0, 0, 0, 0), RealScalar.of(10)) //
    ));
    // ---
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    StateTimeRaster stateTimeRaster = new EtaRaster(eta, x -> x.state().extract(3, 6)); // consider only (beta,r,Ux)
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, plannerConstraint, goalInterface);
    // trajectoryPlanner.represent = x -> x.state().extract(3, 6);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0, 0, 0, 0, 0, 1), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(2000);
    // System.out.println("any=" + glcExpand.getExpandCount());
    int iters = glcExpand.getExpandCount();
    System.out.println("drift iterations:" + iters);
    assertTrue(iters < 3000);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    assertTrue(optional.isPresent());
    glcExpand.untilOptimal(2000);
    System.out.println("opt=" + glcExpand.getExpandCount());
  }
}
