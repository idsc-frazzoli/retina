// code by ynager and jph
package ch.ethz.idsc.gokart.core.plan;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.map.AbstractMapping;
import ch.ethz.idsc.gokart.core.map.ImageGrid;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.pure.CurvePursuitModule;
import ch.ethz.idsc.gokart.core.pure.CurveSe2PursuitLcmClient;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.gokart.lcm.mod.PlannerPublish;
import ch.ethz.idsc.owl.bot.r2.WaypointDistanceCost;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.LexicographicRelabelDecision;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.MinMax;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.order.VectorLexicographic;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

// TODO make configurable as parameter
public abstract class GokartTrajectoryModule extends AbstractClockedModule {
  private static final VehicleModel STANDARD = RimoSinusIonModel.standard();
  private static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  private static final Scalar SPEED = RealScalar.of(2.5);
  private static final FixedStateIntegrator FIXED_STATE_INTEGRATOR = //
      FixedStateIntegrator.create(Se2CarIntegrator.INSTANCE, RationalScalar.of(2, 10), 4);
  private static final Se2Wrap SE2WRAP = Se2Wrap.INSTANCE;
  private static final StateTimeRaster STATE_TIME_RASTER = //
      new EtaRaster(PARTITIONSCALE, StateTimeTensorFunction.state(SE2WRAP::represent));
  // ---
  private final GlobalViewLcmModule globalViewLcmModule = ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);
  private final TrajectoryConfig trajectoryConfig;
  private final FlowsInterface flowsInterface;
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.getProvider();
  protected final CurvePursuitModule curvePursuitModule;
  /** sight lines mapping was successfully used for trajectory planning in a demo on 20190507 */
  private final AbstractMapping<? extends ImageGrid> mapping;
  // = SightLinesMapping.defaultObstacle();
  // GenericBayesianMapping.createObstacleMapping();
  private GokartPoseEvent gokartPoseEvent = null;
  protected List<TrajectorySample> trajectory = null;
  /** waypoints are stored without units */
  private /* final */ Tensor waypoints;
  private PlannerConstraint plannerConstraint;
  private final Tensor goalRadius;
  private Region<Tensor> unionRegion;
  // TODO magic const redundant
  private /* final */ CostFunction waypointCost;
  /** arrives at 50[Hz] */
  private final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;

  public GokartTrajectoryModule(CurvePursuitModule curvePursuitModule) {
    this(TrajectoryConfig.GLOBAL, curvePursuitModule);
  }

  public GokartTrajectoryModule(TrajectoryConfig trajectoryConfig, CurvePursuitModule curvePursuitModule) {
    this.trajectoryConfig = trajectoryConfig;
    this.curvePursuitModule = curvePursuitModule;
    flowsInterface = Se2CarFlows.forward(SPEED, Magnitude.PER_METER.apply(trajectoryConfig.maxRotation));
    mapping = trajectoryConfig.getAbstractMapping();
    MinMax minMax = MinMax.of(STANDARD.footprint());
    Tensor x_samples = Subdivide.of(minMax.min().get(0), minMax.max().get(0), 2); // {-0.295, 0.7349999999999999, 1.765}
    PredefinedMap predefinedMap = TrajectoryConfig.getPredefinedMapObstacles();
    ImageRegion imageRegion = predefinedMap.getImageRegion();
    // ---
    unionRegion = RegionUnion.wrap(Arrays.asList( //
        Se2PointsVsRegions.line(x_samples, imageRegion), mapping.getMap()));
    plannerConstraint = RegionConstraints.timeInvariant(unionRegion);
    // ---
    final Scalar goalRadius_xy = SQRT2.divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITIONSCALE.Get(2));
    goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta);
  }

  /* package for testing */ synchronized void updateWaypoints(Tensor curve) {
    waypoints = Tensor.of(trajectoryConfig.resampledWaypoints(curve).stream().map(PoseHelper::toUnitless));
    waypointCost = WaypointDistanceCost.of( //
        Nest.of(new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE)::cyclic, waypoints, 1), //
        true, // 1 round of refinement
        RealScalar.of(1), // width of virtual lane in model coordinates
        RealScalar.of(7.5), // model2pixel conversion factor
        new Dimension(640, 640)); // resolution of image
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setWaypoints(waypoints);
  }

  public ImageGrid obstacleMapping() {
    return mapping.getMap();
  }

  @Override // from AbstractClockedModule
  protected void first() {
    mapping.start();
    // ---
    gokartPoseLcmClient.addListener(gokartPoseListener);
    gokartPoseLcmClient.startSubscriptions();
    // ---
    curveSe2PursuitLcmClient.addListener(this::updateWaypoints);
    curveSe2PursuitLcmClient.startSubscriptions();
    // ---
    curvePursuitModule.launch();
  }

  @Override // from AbstractClockedModule
  protected void last() {
    curvePursuitModule.terminate();
    gokartPoseLcmClient.stopSubscriptions();
    // ---
    mapping.stop();
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setWaypoints(null);
  }

  @Override // from AbstractClockedModule
  protected synchronized void runAlgo() {
    System.out.println("entering...");
    mapping.prepareMap();
    if (Objects.nonNull(gokartPoseEvent))
      if (Objects.nonNull(waypoints)) {
        final Scalar tangentSpeed = gokartPoseEvent.getVelocity().Get(0);
        System.out.println("setup planner, tangent speed=" + tangentSpeed);
        final Tensor xya = PoseHelper.toUnitless(gokartPoseEvent.getPose()).unmodifiable();
        final List<TrajectorySample> head;
        Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
        boolean isResetPressed = optional.isPresent() && optional.get().isResetPressed();
        if (Objects.isNull(trajectory) || isResetPressed) { // exists previous trajectory?
          // no: plan from current position
          System.out.println("plan from current position");
          StateTime stateTime = new StateTime(xya, RealScalar.ZERO);
          head = Collections.singletonList(TrajectorySample.head(stateTime));
        } else {
          // yes: plan from closest point + cutoffDist on previous trajectory
          System.out.println("plan from closest point + cutoffDist on previous trajectory");
          Scalar cutoffDist = trajectoryConfig.getCutoffDistance(tangentSpeed);
          head = getTrajectoryUntil(trajectory, xya, Magnitude.METER.apply(cutoffDist));
        }
        Tensor distances = Tensor.of(waypoints.stream().map(wp -> Norm._2.ofVector(SE2WRAP.difference(wp, xya))));
        int wpIdx = ArgMin.of(distances); // find closest waypoint to current position
        if (head.isEmpty()) {
          System.err.println("head is empty");
        } else //
        if (0 <= wpIdx) { // jan inserted check for non-empty
          Tensor goal = waypoints.get(wpIdx);
          // find a goal waypoint that is located beyond horizonDistance & does not lie within obstacle
          int count = 0;
          while (Scalars.lessThan(Norm._2.ofVector(SE2WRAP.difference(xya, goal)), trajectoryConfig.horizonDistance) || unionRegion.isMember(goal)) {
            wpIdx = (wpIdx + 1) % waypoints.length();
            goal = waypoints.get(wpIdx);
            ++count;
            if (waypoints.length() < count) {
              // TODO JPH
              System.err.println("panic: infinite look prevention");
              break;
            }
          }
          // System.out.format("goal index = " + wpIdx + ", distance = %.2f \n", SE2WRAP.distance(xya, goal).number().floatValue());
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
          TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
              STATE_TIME_RASTER, FIXED_STATE_INTEGRATOR, controls, //
              plannerConstraint, multiCostGoalInterface, //
              new LexicographicRelabelDecision(VectorLexicographic.COMPARATOR));
          // Do Planning
          StateTime root = Lists.getLast(head).stateTime(); // non-empty due to check above
          trajectoryPlanner.insertRoot(root);
          new Expand<>(trajectoryPlanner).maxTime(trajectoryConfig.expandTimeLimit());
          expandResult(head, trajectoryPlanner); // build detailed trajectory and pass to purePursuit
          return;
        } else {
          System.err.println("argmin index negative");
        }
      }
    else
      System.err.println("no curve because no pose");
    curvePursuitModule.setCurve(Optional.empty());
    PlannerPublish.publishTrajectory(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME, new ArrayList<>());
  }

  /** @param trajectory
   * @param pose
   * @param cutoffDistHead non-negative unit-less
   * @return
   * @throws Exception if cutoffDistHead is negative */
  private static List<TrajectorySample> getTrajectoryUntil( //
      List<TrajectorySample> trajectory, Tensor pose, Scalar cutoffDistHead) {
    Sign.requirePositiveOrZero(cutoffDistHead);
    Tensor distances = Tensor.of(trajectory.stream() //
        .map(trajectorySample -> Norm._2.ofVector(SE2WRAP.difference(trajectorySample.stateTime().state(), pose))));
    int closestIdx = ArgMin.of(distances);
    Tensor closest = trajectory.get(closestIdx).stateTime().state();
    return trajectory.stream() //
        .skip(Math.max(closestIdx - 5, 0)) // TODO magic const
        .filter(trajectorySample -> Scalars.lessEquals( //
            Norm._2.ofVector(SE2WRAP.difference(closest, trajectorySample.stateTime().state())), cutoffDistHead)) //
        .collect(Collectors.toList());
  }

  @Override // from AbstractClockedModule
  protected final Scalar getPeriod() {
    return trajectoryConfig.planningPeriod;
  }

  Collection<Flow> getFlows(int resolution) {
    return flowsInterface.getFlows(resolution);
  }

  public List<TrajectorySample> currentTrajectory() {
    return Collections.unmodifiableList(trajectory);
  }

  protected abstract void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner);
}
