// code by ynager and jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.gokart.core.map.GokartMappingModule;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.mod.PlannerPublish;
import ch.ethz.idsc.owl.bot.r2.ImageCostFunction;
import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.bot.se2.glc.CarFlows;
import ch.ethz.idsc.owl.bot.se2.glc.CarForwardFlows;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class GokartTrajectoryModule extends AbstractClockedModule implements GokartPoseListener {
  // TODO make configurable as parameter
  private static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  private static final Scalar SPEED = RealScalar.of(2.5);
  private static final Tensor VIRTUAL = Tensors.fromString("{{38, 39}, {42, 47}, {51, 52}, {46, 43}}");
  /** rotation per meter driven is at least 23[deg/m]
   * 20180429_minimum_turning_radius.pdf
   * 20180517 reduced radius from 23 to 20 to be more conservative and avoid extreme steering */
  static final CarFlows CARFLOWS = new CarForwardFlows(SPEED, Degree.of(20));
  static final FixedStateIntegrator FIXEDSTATEINTEGRATOR = // node interval == 2/5
      FixedStateIntegrator.create(Se2CarIntegrator.INSTANCE, RationalScalar.of(2, 10), 4);
  static final Se2Wrap SE2WRAP = new Se2Wrap(Tensors.vector(1, 1, 2));
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private Collection<CostFunction> costCollection = new LinkedList<>();
  final PurePursuitModule purePursuitModule = new PurePursuitModule();
  final GokartMappingModule gokartMappingModule = new GokartMappingModule();
  private Region<Tensor> fixedRegion;
  private Region<Tensor> polygonRegion;
  private GokartPoseEvent gokartPoseEvent = null;
  private List<TrajectorySample> trajectory = null;
  Tensor obstacleMap;
  Tensor waypoints;
  private PlannerConstraint plannerConstraint;
  private Tensor goalRadius;
  private Region<Tensor> unionRegion;
  private Scalar tangentSpeed = null;
  private RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent getEvent) {
      tangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
    }
  };

  @Override // from AbstractClockedModule
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    purePursuitModule.launch();
    // ---
    rimoGetLcmClient.addListener(rimoGetListener);
    rimoGetLcmClient.startSubscriptions();
    // ---
    obstacleMap = ImageRegions.grayscale(ResourceData.of("/map/dubendorf/hangar/20180423obstacles.png"));
    Tensor tensor = ImageEdges.extrusion(obstacleMap, 6); // == 0.73 * 7.5 == 5.475
    final Scalar scale = DoubleScalar.of(7.5); // meter_to_pixel
    Tensor range = Tensors.vector(Dimensions.of(tensor)).divide(scale);
    ImageRegion imageRegion = new ImageRegion(tensor, range, false);
    // TODO obtain magic const from footprint
    fixedRegion = Se2PointsVsRegions.line(Tensors.vector(-0.3, 0.8, 1.77), imageRegion);
    polygonRegion = PolygonRegion.of(VIRTUAL); // virtual obstacle in middle
    // ---
    waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
    // plannerConstraint = RegionConstraints.timeInvariant(unionRegion);
    costCollection.add(ImageCostFunction.of(tensor, range, RealScalar.ZERO));
    costCollection.add(new Se2LateralAcceleration(RealScalar.of(2)));
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
    gokartMappingModule.prepareMap();
    unionRegion = RegionUnion.wrap(Arrays.asList(fixedRegion, gokartMappingModule));
    plannerConstraint = RegionConstraints.timeInvariant(gokartMappingModule);
    Scalar tangentSpeed_ = tangentSpeed;
    if (Objects.nonNull(gokartPoseEvent) && Objects.nonNull(tangentSpeed_)) {
      System.out.println("setup planner");
      final Tensor xya = GokartPoseHelper.toUnitless(gokartPoseEvent.getPose()).unmodifiable();
      final List<TrajectorySample> head;
      if (Objects.isNull(trajectory)) { // exists previous trajectory?
        // no: plan from current position
        StateTime stateTime = new StateTime(xya, RealScalar.ZERO);
        head = Arrays.asList(TrajectorySample.head(stateTime));
      } else {
        // yes: plan from closest point + cutoffDist on previous trajectory
        tangentSpeed_ = Ramp.FUNCTION.apply(tangentSpeed_); // with unit "m*s^-1"
        Scalar cutoffDist = tangentSpeed_ //
            .multiply(TrajectoryConfig.GLOBAL.planningPeriod) //
            .add(Quantity.of(2.5, SI.METER)); // TODO magic const
        head = getTrajectoryUntil(trajectory, xya, Magnitude.METER.apply(cutoffDist));
      }
      Tensor distances = Tensor.of(waypoints.stream().map(wp -> SE2WRAP.distance(wp, xya)));
      int wpIdx = ArgMin.of(distances); // find closest waypoint to current position
      if (0 <= wpIdx && !head.isEmpty()) { // jan inserted check for non-empty
        Tensor goal = waypoints.get(wpIdx);
        // find a goal waypoint that is located beyond horizonDistance & does not lie within obstacle
        while (Scalars.lessThan(SE2WRAP.distance(xya, goal), TrajectoryConfig.GLOBAL.horizonDistance) || unionRegion.isMember(goal)) {
          wpIdx = (wpIdx + 1) % waypoints.length();
          goal = waypoints.get(wpIdx);
        }
        System.out.format("goal index = " + wpIdx + ",  distance = %.2f \n", SE2WRAP.distance(xya, goal).number().floatValue());
        Collection<Flow> controls = CARFLOWS.getFlows(9); // TODO magic const
        // Se2ComboRegion se2ComboRegion = Se2ComboRegion.spherical(goal, goalRadius);
        Se2ComboRegion se2ComboRegion = Se2ComboRegion.cone(goal, RealScalar.of(Math.PI / 10), goalRadius.Get(2));
        GoalInterface goalInterface = new Se2MinTimeGoalManager(se2ComboRegion, controls).getGoalInterface();
        GoalInterface multiCostGoalInterface = MultiCostGoalAdapter.of(goalInterface, costCollection);
        TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
            PARTITIONSCALE, FIXEDSTATEINTEGRATOR, controls, plannerConstraint, multiCostGoalInterface);
        trajectoryPlanner.represent = StateTimeTensorFunction.state(SE2WRAP::represent);
        // Do Planning
        StateTime root = Lists.getLast(head).stateTime(); // non-empty due to check above
        trajectoryPlanner.insertRoot(root);
        Expand.maxTime(trajectoryPlanner, getPeriod().multiply(Scalars.fromString("3/4"))); // TODO magic
        expandResult(head, trajectoryPlanner); // build detailed trajectory and pass to purePursuit
        return;
      }
    }
    purePursuitModule.setCurve(Optional.empty());
    System.err.println("no curve because no pose");
  }

  /** @param trajectory
   * @param pose
   * @param cutoffDistHead non-negative unit-less
   * @return
   * @throws Exception if cutoffDistHead is negative */
  private static List<TrajectorySample> getTrajectoryUntil( //
      List<TrajectorySample> trajectory, Tensor pose, Scalar cutoffDistHead) {
    Sign.requirePositiveOrZero(cutoffDistHead);
    Tensor distances = Tensor.of(trajectory.stream().map(st -> SE2WRAP.distance(st.stateTime().state(), pose)));
    int closestIdx = ArgMin.of(distances);
    Tensor closest = trajectory.get(closestIdx).stateTime().state();
    return trajectory.stream() //
        .skip(Math.max((closestIdx - 5), 0)) // TODO magic const
        .filter(trajectorySample -> Scalars.lessEquals( //
            SE2WRAP.distance(closest, trajectorySample.stateTime().state()), cutoffDistHead)) //
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

  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) { // goal reached
      List<TrajectorySample> tail = //
          GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
      trajectory = Trajectories.glue(head, tail);
      Tensor curve = Tensor.of(trajectory.stream().map(ts -> ts.stateTime().state().extract(0, 2)));
      purePursuitModule.setCurve(Optional.of(curve));
      PlannerPublish.publishTrajectory(trajectory);
    } else {
      // failure to reach goal
      purePursuitModule.setCurve(Optional.empty());
      PlannerPublish.publishTrajectory(new ArrayList<>());
    }
  }
}
