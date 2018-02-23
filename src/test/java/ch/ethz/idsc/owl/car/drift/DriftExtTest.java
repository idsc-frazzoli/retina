// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owl.math.region.HyperplaneRegion;
import ch.ethz.idsc.owl.math.region.NegativeHalfspaceRegion;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
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
    Collection<Flow> controls = DriftControls.createExtended(10);
    GoalInterface goalInterface = DriftGoalManager.createStandard(//
        Tensors.vector(0, 0, 0, -0.3055, 0.5032, 8), //
        Tensors.vector( //
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, //
            0.05, 0.05, 0.25));
    // ---
    TrajectoryRegionQuery obstacleQuery = SimpleTrajectoryRegionQuery.timeInvariant( //
        RegionUnion.wrap(Arrays.asList( //
            new NegativeHalfspaceRegion(4) // ensure that r is non-negative
            , // impose that x < Threshold
            new HyperplaneRegion(Tensors.vector(-1, 0, 0, 0, 0, 0), RealScalar.of(10)) //
        )));
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta, stateIntegrator, controls, obstacleQuery, goalInterface);
    trajectoryPlanner.represent = x -> x.state().extract(3, 6); // consider only (beta,r,Ux)
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0, 0, 0, 0, 0, 1), RealScalar.ZERO));
    int iters = Expand.maxSteps(trajectoryPlanner, 2000);
    System.out.println("drift iterations:" + iters);
    assertTrue(iters < 1900);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    assertTrue(optional.isPresent());
  }
}
