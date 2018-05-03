// code by ynager and jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.TrajectoryRender;
import ch.ethz.idsc.owl.bot.r2.ImageCostFunction;
import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.bot.se2.glc.CarFlows;
import ch.ethz.idsc.owl.bot.se2.glc.CarForwardFlows;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.win.MotionPlanWorker;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class GokartTrajectoryModule extends AbstractClockedModule implements //
    GokartPoseListener, GlcPlannerCallback {
  // TODO make configurable as parameter
  private static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  private static final Scalar SPEED = RealScalar.of(2.5);
  private static final Tensor VIRTUAL = Tensors.fromString("{{38, 39}, {42, 47}, {51, 52}, {46, 43}}");
  /** rotation per meter driven is at least 23[deg/m]
   * 20180429_minimum_turning_radius.pdf */
  static final CarFlows CARFLOWS = new CarForwardFlows(SPEED, Degree.of(23));
  static final FixedStateIntegrator FIXEDSTATEINTEGRATOR = // node interval == 2/5
      FixedStateIntegrator.create(Se2CarIntegrator.INSTANCE, RationalScalar.of(2, 10), 4);
  static final Se2Wrap SE2WRAP = new Se2Wrap(Tensors.vector(1, 1, 2));
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private Collection<CostFunction> costCollection = new LinkedList<>();
  final PurePursuitModule purePursuitModule = new PurePursuitModule();
  private GokartPoseEvent gokartPoseEvent = null;
  private List<TrajectorySample> trajectory = null;
  Tensor obstacleMap;
  Tensor waypoints;
  private PlannerConstraint plannerConstraint;
  private Tensor goalRadius;
  MotionPlanWorker motionPlanWorker;

  @Override // from AbstractClockedModule
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    purePursuitModule.launch();
    // TODO initialze cost funct
    obstacleMap = ImageRegions.grayscale(ResourceData.of("/map/dubendorf/hangar/20180423obstacles.png"));
    Tensor tensor = ImageEdges.extrusion(obstacleMap, 6); // == 0.73 * 7.5 == 5.475
    final Scalar scale = DoubleScalar.of(7.5); // meter_to_pixel
    Tensor range = Tensors.vector(Dimensions.of(tensor)).divide(scale);
    ImageRegion imageRegion = new ImageRegion(tensor, range, false);
    // TODO obtain magic const from footprint
    Region<Tensor> region = Se2PointsVsRegions.line(Tensors.vector(-0.3, 0.8, 1.77), imageRegion);
    Region<Tensor> polygonRegion = PolygonRegion.of(VIRTUAL); // virtual obstacle
    Region<Tensor> union = RegionUnion.wrap(Arrays.asList(region, polygonRegion));
    // ---
    waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
    plannerConstraint = RegionConstraints.timeInvariant(union);
    costCollection.add(ImageCostFunction.of(tensor, range, RealScalar.ZERO));
    // ---
    final Scalar goalRadius_xy = SQRT2.divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITIONSCALE.Get(2));
    goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta);
  }

  @Override // from AbstractClockedModule
  protected void last() {
    purePursuitModule.terminate();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    if (Objects.nonNull(gokartPoseEvent)) {
      if (Objects.nonNull(motionPlanWorker)) {
        motionPlanWorker.flagShutdown();
        motionPlanWorker = null;
      }
      System.out.println("setup planner");
      final Tensor xya = GokartPoseHelper.toUnitless(gokartPoseEvent.getPose()).unmodifiable();
      final List<TrajectorySample> head;
      if (Objects.isNull(trajectory)) { // has prev traj ?
        // no: plan from current position to "best waypoint")
        StateTime stateTime = new StateTime(xya, RealScalar.ZERO);
        head = Arrays.asList(TrajectorySample.head(stateTime));
      } else {
        // yes: find closest point on previous traj+delay... then plan to "best waypoint"
        Tensor distances = Tensor.of(trajectory.stream().map(st -> SE2WRAP.distance(st.stateTime().state(), xya)));
        int closestIdx = ArgMin.of(distances);
        StateTime closestStateTime = trajectory.get(closestIdx).stateTime();
        head = getTrajectoryUntil(trajectory, closestIdx, //
            closestStateTime.time().add(Magnitude.SECOND.apply(TrajectoryConfig.GLOBAL.planningPeriod)));
      }
      // find a goal waypoint that is located at the horizonDistance
      Tensor distances = Tensor.of(waypoints.stream().map(wp -> SE2WRAP.distance(wp, xya)));
      int wpIdx = ArgMin.of(distances); // find closest waypoint to current position
      if (0 <= wpIdx) {
        Tensor goal = waypoints.get(wpIdx);
        while (Scalars.lessThan(SE2WRAP.distance(xya, goal), TrajectoryConfig.GLOBAL.horizonDistance)) {
          wpIdx = (wpIdx + 1) % waypoints.length();
          goal = waypoints.get(wpIdx);
        }
        System.out.format("goal index = " + wpIdx + ",  distance = %.2f", SE2WRAP.distance(xya, goal).number().floatValue());
        Collection<Flow> controls = CARFLOWS.getFlows(9); // TODO magic const
        GoalInterface goalInterface = Se2MinTimeGoalManager.create(goal, goalRadius, controls);
        GoalInterface multiCostGoalInterface = MultiCostGoalAdapter.of(goalInterface, costCollection);
        TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
            PARTITIONSCALE, FIXEDSTATEINTEGRATOR, controls, plannerConstraint, multiCostGoalInterface);
        trajectoryPlanner.represent = StateTimeTensorFunction.state(SE2WRAP::represent);
        motionPlanWorker = new MotionPlanWorker();
        motionPlanWorker.addCallback(this);
        // plan from root/tail to goal
        motionPlanWorker.start(head, trajectoryPlanner);
        // System.out.println("started");
        // in call back set curve
        return;
      }
    }
    // no pose -> no traj
    // set curve to optional.empty
    purePursuitModule.setCurve(Optional.empty());
    System.err.println("no curve because no pose");
  }

  private static List<TrajectorySample> getTrajectoryUntil( //
      List<TrajectorySample> trajectory, int tailIdx, Scalar abs_cutoff) {
    return trajectory.stream() //
        .skip(tailIdx) //
        .filter(trajectorySample -> Scalars.lessEquals(trajectorySample.stateTime().time(), abs_cutoff)) //
        .collect(Collectors.toList());
  }

  @Override
  protected Scalar getPeriod() {
    return TrajectoryConfig.GLOBAL.planningPeriod;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) { // arrives at 50[Hz]
    this.gokartPoseEvent = gokartPoseEvent;
  }

  @Override // from GlcPlannerCallback
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    // System.out.println("CALLBACK ");
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      trajectory = //
          GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
      Tensor curve = Tensor.of(trajectory.stream().map(ts -> ts.stateTime().state().extract(0, 2)));
      TrajectoryRender.TRAJECTORY = trajectory;
      purePursuitModule.setCurve(Optional.of(curve));
      // System.out.println("yey! assigned curve length == " + curve.length());
    } else {
      // failure to reach goal
      TrajectoryRender.TRAJECTORY = null;
      purePursuitModule.setCurve(Optional.empty());
    }
  }
}
