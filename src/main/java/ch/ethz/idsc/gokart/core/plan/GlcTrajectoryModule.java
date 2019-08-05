// code by ynager and jph
package ch.ethz.idsc.gokart.core.plan;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.gokart.core.pure.CurvePursuitModule;
import ch.ethz.idsc.owl.bot.r2.WaypointDistanceCost;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.LexicographicRelabelDecision;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.order.VectorLexicographic;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Sqrt;

// TODO make configurable as parameter
public abstract class GlcTrajectoryModule extends GokartTrajectoryModule<TrajectoryPlanner> {
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  private static final Scalar SPEED = RealScalar.of(2.5);
  private static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  private static final FixedStateIntegrator FIXED_STATE_INTEGRATOR = //
      FixedStateIntegrator.create(Se2CarIntegrator.INSTANCE, RationalScalar.of(2, 10), 4);
  private static final StateTimeRaster STATE_TIME_RASTER = //
      new EtaRaster(PARTITIONSCALE, StateTimeTensorFunction.state(SE2WRAP::represent));
  // ---
  private final FlowsInterface flowsInterface;
  private final Tensor goalRadius;
  private PlannerConstraint plannerConstraint;
  // TODO magic const redundant
  private CostFunction waypointCost;

  public GlcTrajectoryModule(CurvePursuitModule curvePursuitModule) {
    this(TrajectoryConfig.GLOBAL, curvePursuitModule);
  }

  public GlcTrajectoryModule(TrajectoryConfig trajectoryConfig, CurvePursuitModule curvePursuitModule) {
    super(trajectoryConfig, curvePursuitModule);
    flowsInterface = Se2CarFlows.forward(SPEED, Magnitude.PER_METER.apply(trajectoryConfig.maxRotation));
    plannerConstraint = RegionConstraints.timeInvariant(unionRegion);
    // ---
    final Scalar goalRadius_xy = SQRT2.divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITIONSCALE.Get(2));
    goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta);
  }

  /* package for testing */ synchronized void updateWaypoints(Tensor curve) {
    super.updateWaypoints(curve);
    waypointCost = WaypointDistanceCost.of( //
        Nest.of(new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE)::cyclic, waypoints, 1), //
        true, // 1 round of refinement
        RealScalar.of(1), // width of virtual lane in model coordinates
        RealScalar.of(7.5), // model2pixel conversion factor
        new Dimension(640, 640)); // resolution of image
  }

  @Override // from GokartTrajectoryModule
  protected final TrajectoryPlanner setupTreePlanner(StateTime root, Tensor goal) {
    int resolution = trajectoryConfig.controlResolution.number().intValue();
    Collection<Flow> controls = flowsInterface.getFlows(resolution);
    // goalRadius.pmul(Tensors.vector(2, 2, 1));
    // System.out.println(goalRadius);
    Se2ComboRegion se2ComboRegion = //
        // Se2ComboRegion.spherical(goal, goalRadius.pmul(TrajectoryConfig.GLOBAL.goalRadiusFactor));
        Se2ComboRegion.cone(goal, trajectoryConfig.coneHalfAngle, goalRadius.Get(2));
    // ---
    // GoalInterface goalInterface = new Se2MinTimeGoalManager(se2ComboRegion, controls).getGoalInterface();
    // GoalInterface multiCostGoalInterface = MultiCostGoalAdapter.of(goalInterface, costCollection);
    List<CostFunction> costs = new ArrayList<>();
    costs.add(waypointCost);
    costs.add(new Se2MinTimeGoalManager(se2ComboRegion, controls));
    GoalInterface multiCostGoalInterface = new VectorCostGoalAdapter(costs, se2ComboRegion);
    return new StandardTrajectoryPlanner( //
        STATE_TIME_RASTER, FIXED_STATE_INTEGRATOR, controls, //
        plannerConstraint, multiCostGoalInterface, //
        new LexicographicRelabelDecision(VectorLexicographic.COMPARATOR));
  }

  Collection<Flow> getFlows(int resolution) {
    return flowsInterface.getFlows(resolution);
  }
}
