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
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
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
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
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
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class GokartTrajectoryModule extends AbstractClockedModule implements GokartPoseListener, JoystickListener {
  private static final VehicleModel STANDARD = RimoSinusIonModel.standard();
  // TODO make configurable as parameter
  private static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  private static final Scalar SPEED = RealScalar.of(2.5);
  private static final Tensor VIRTUAL = Tensors.fromString("{{38, 39}, {42, 47}, {51, 52}, {46, 43}}");
  private static final FixedStateIntegrator FIXEDSTATEINTEGRATOR = // node interval == 2/5
      FixedStateIntegrator.create(Se2CarIntegrator.INSTANCE, RationalScalar.of(2, 10), 4);
  private static final Se2Wrap SE2WRAP = new Se2Wrap(Tensors.vector(1, 1, 2));
  // ---
  final FlowsInterface carFlows = CarFlows.forward( //
      SPEED, Magnitude.PER_METER.apply(TrajectoryConfig.GLOBAL.maxRotation));
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  private GokartJoystickInterface joystickInterface;
  private final Collection<CostFunction> costCollection = new LinkedList<>();
  final PurePursuitModule purePursuitModule = new PurePursuitModule();
  private final GokartMappingModule gokartMappingModule = new GokartMappingModule();
  private final Region<Tensor> fixedRegion;
  private final Region<Tensor> polygonRegion;
  private GokartPoseEvent gokartPoseEvent = null;
  private List<TrajectorySample> trajectory = null;
  public final Tensor obstacleMap;
  final Tensor waypoints;
  private PlannerConstraint plannerConstraint;
  private final Tensor goalRadius;
  private Region<Tensor> unionRegion;
  private Scalar tangentSpeed = null;
  private RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent getEvent) {
      tangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
    }
  };

  public GokartTrajectoryModule() {
    PredefinedMap predefinedMap = PredefinedMap.DUBENDORF_HANGAR_20180423OBSTACLES;
    obstacleMap = ImageRegions.grayscale(ResourceData.of("/map/dubendorf/hangar/20180423obstacles.png"));
    Tensor hull = STANDARD.footprint();
    Tensor min = hull.stream().reduce(Entrywise.min()).get(); // {-0.295, -0.725, -0.25}
    Tensor max = hull.stream().reduce(Entrywise.max()).get(); // {1.765, 0.725, -0.25}
    int ttl = Ceiling.of(max.Get(1).multiply(predefinedMap.scale())).number().intValue(); // == 0.73 * 7.5 == 5.475 => 6
    Tensor tensor = ImageEdges.extrusion(obstacleMap, ttl);
    ImageRegion imageRegion = predefinedMap.getImageRegion();
    Tensor x_samples = Subdivide.of(min.get(0), max.get(0), 2); // {-0.295, 0.7349999999999999, 1.765}
    fixedRegion = Se2PointsVsRegions.line(x_samples, imageRegion);
    polygonRegion = PolygonRegion.of(VIRTUAL); // virtual obstacle in middle
    // ---
    waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
    unionRegion = RegionUnion.wrap(Arrays.asList(fixedRegion, gokartMappingModule, polygonRegion));
    plannerConstraint = RegionConstraints.timeInvariant(unionRegion);
    costCollection.add(ImageCostFunction.of(tensor, predefinedMap.range(), RealScalar.ZERO));
    costCollection.add(new Se2LateralAcceleration(RealScalar.of(2)));
    // ---
    final Scalar goalRadius_xy = SQRT2.divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITIONSCALE.Get(2));
    goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta);
  }

  @Override // from AbstractClockedModule
  protected void first() throws Exception {
    gokartMappingModule.start();
    // ---
    gokartPoseLcmClient.addListener(this);
    joystickLcmClient.addListener(this);
    rimoGetLcmClient.addListener(rimoGetListener);
    // ---
    gokartPoseLcmClient.startSubscriptions();
    joystickLcmClient.startSubscriptions();
    rimoGetLcmClient.startSubscriptions();
    // ---
    purePursuitModule.launch();
  }

  @Override // from AbstractClockedModule
  protected void last() {
    purePursuitModule.terminate();
    gokartPoseLcmClient.stopSubscriptions();
    joystickLcmClient.stopSubscriptions();
    // ---
    gokartMappingModule.stop();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    gokartMappingModule.prepareMap();
    Scalar tangentSpeed_ = tangentSpeed;
    if (Objects.nonNull(gokartPoseEvent) && Objects.nonNull(tangentSpeed_)) {
      System.out.println("setup planner");
      final Tensor xya = GokartPoseHelper.toUnitless(gokartPoseEvent.getPose()).unmodifiable();
      final List<TrajectorySample> head;
      if (Objects.isNull(trajectory) || joystickInterface.isResetPressed()) { // exists previous trajectory?
        // no: plan from current position
        StateTime stateTime = new StateTime(xya, RealScalar.ZERO);
        head = Arrays.asList(TrajectorySample.head(stateTime));
      } else {
        // yes: plan from closest point + cutoffDist on previous trajectory
        Scalar cutoffDist = TrajectoryConfig.GLOBAL.getCutoffDistance(tangentSpeed_);
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
        // System.out.format("goal index = " + wpIdx + ", distance = %.2f \n", SE2WRAP.distance(xya, goal).number().floatValue());
        int resolution = TrajectoryConfig.GLOBAL.controlResolution.number().intValue();
        Collection<Flow> controls = carFlows.getFlows(resolution);
        Se2ComboRegion se2ComboRegion = //
            Se2ComboRegion.cone(goal, TrajectoryConfig.GLOBAL.coneHalfAngle, goalRadius.Get(2));
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
  protected final Scalar getPeriod() {
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

  @Override // from JoystickListener
  public void joystick(JoystickEvent joystickEvent) {
    joystickInterface = (GokartJoystickInterface) joystickEvent;
  }
}
