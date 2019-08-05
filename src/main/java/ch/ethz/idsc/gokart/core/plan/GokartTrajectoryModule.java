// code by ynager and jph
package ch.ethz.idsc.gokart.core.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.tree.TreePlanner;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.math.MinMax;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

// TODO make configurable as parameter
public abstract class GokartTrajectoryModule<T extends TreePlanner> extends AbstractClockedModule {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  protected static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  protected static final Se2Wrap SE2WRAP = Se2Wrap.INSTANCE;
  // ---
  private final GlobalViewLcmModule globalViewLcmModule = ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.getProvider();
  /** arrives at 50[Hz] */
  private final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;
  /** sight lines mapping was successfully used for trajectory planning in a demo on 20190507 */
  protected final AbstractMapping<? extends ImageGrid> mapping;
  // = SightLinesMapping.defaultObstacle();
  // GenericBayesianMapping.createObstacleMapping();
  protected final TrajectoryConfig trajectoryConfig;
  protected final CurvePursuitModule curvePursuitModule;
  protected final Tensor goalRadius;
  // ---
  private GokartPoseEvent gokartPoseEvent = null;
  protected List<TrajectorySample> trajectory = null;
  /** waypoints are stored without units */
  protected Tensor waypoints;
  protected Region<Tensor> unionRegion;

  public GokartTrajectoryModule(TrajectoryConfig trajectoryConfig, CurvePursuitModule curvePursuitModule) {
    this.trajectoryConfig = trajectoryConfig;
    this.curvePursuitModule = curvePursuitModule;
    mapping = trajectoryConfig.getAbstractMapping();
    MinMax minMax = MinMax.of(VEHICLE_MODEL.footprint());
    Tensor x_samples = Subdivide.of(minMax.min().get(0), minMax.max().get(0), 2); // {-0.295, 0.7349999999999999, 1.765}
    PredefinedMap predefinedMap = TrajectoryConfig.getPredefinedMapObstacles();
    Region<Tensor> predefinedObstacles = Se2PointsVsRegions.line(x_samples, predefinedMap.getImageRegion()); // contains known static obstacles
    // ---
    unionRegion = RegionUnion.wrap(Arrays.asList(predefinedObstacles, mapping.getMap()));
    // ---
    final Scalar goalRadius_xy = SQRT2.divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITIONSCALE.Get(2));
    goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta);
  }

  /* package for testing */ synchronized void updateWaypoints(Tensor curve) {
    waypoints = Tensor.of(trajectoryConfig.resampledWaypoints(curve, true).stream().map(PoseHelper::toUnitless));
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setWaypoints(waypoints);
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
    if (Objects.nonNull(gokartPoseEvent)) {
      if (Objects.nonNull(waypoints) && Tensors.nonEmpty(waypoints)) {
        mapping.prepareMap();
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
          // yes: try to plan from closest point + cutoffDist on previous trajectory
          Scalar cutoffDist = trajectoryConfig.getCutoffDistance(tangentSpeed);
          head = getTrajectoryUntil(xya, Magnitude.METER.apply(cutoffDist)).map(trj -> { // is the previous trajectory still valid?
            System.out.println("plan from closest point + cutoffDist on previous trajectory"); // yes
            return trj;
          }).orElseGet(() -> {
            System.out.println("plan from current position"); // no
            StateTime stateTime = new StateTime(xya, RealScalar.ZERO);
            return Collections.singletonList(TrajectorySample.head(stateTime));
          });
        }
        if (head.isEmpty()) {
          System.err.println("head is empty");
        } else {
          Predicate<Tensor> conflicts = goal -> //
          Scalars.lessEquals(Norm._2.ofVector(SE2WRAP.difference(xya, goal)), trajectoryConfig.horizonDistance) || unionRegion.isMember(goal);
          Iterator<Tensor> iterator = RotateLeft.of(waypoints, locate(waypoints, xya)).iterator();
          Tensor goal = iterator.next();
          // TODO GJOEL/JPH criterion is too primitive
          while (iterator.hasNext() && conflicts.test(goal))
            goal = iterator.next();
          if (conflicts.test(goal))
            System.err.println("no feasible goal found"); // TODO JPH
          else {
            // Do Planning
            StateTime root = Lists.getLast(head).stateTime(); // non-empty due to check above
            T treePlanner = setupTreePlanner(root, goal);
            treePlanner.insertRoot(root);
            new Expand<>(treePlanner).maxTime(trajectoryConfig.expandTimeLimit());
            expandResult(head, treePlanner); // build detailed trajectory and pass to purePursuit
          }
          return;
        }
      } else
        System.err.println("no curve because no waypoints: " + waypoints);
    } else
      System.err.println("no curve because no pose");
    curvePursuitModule.setCurve(Optional.empty());
    PlannerPublish.trajectory(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME, new ArrayList<>());
  }

  /** @param pose
   * @param cutoffDistHead non-negative unit-less
   * @return
   * @throws Exception if cutoffDistHead is negative, or no waypoints are present */
  private Optional<List<TrajectorySample>> getTrajectoryUntil(Tensor pose, Scalar cutoffDistHead) {
    int closestIdx = locate(trajectory, pose);
    Tensor closest = trajectory.get(closestIdx).stateTime().state();
    if (Scalars.lessThan(Norm._2.ofVector(SE2WRAP.difference(pose, closest)), trajectoryConfig.proximityDistance)) {
      return Optional.of(trajectory.stream() //
          .skip(Math.max(closestIdx - 5, 0)) // TODO magic const
          .filter(trajectorySample -> Scalars.lessEquals( //
              Norm._2.ofVector(SE2WRAP.difference(closest, trajectorySample.stateTime().state())), Sign.requirePositiveOrZero(cutoffDistHead))) //
          .collect(Collectors.toList()));
    }
    return Optional.empty();
  }

  protected static int locate(Collection<TrajectorySample> trajectory, Tensor state) {
    if (Objects.isNull(trajectory) || trajectory.isEmpty()) {
      trajectory.forEach(System.err::println);
      throw TensorRuntimeException.of(state);
    }
    return locate(trajectory.stream().map(TrajectorySample::stateTime).map(StateTime::state), state);
  }

  protected static int locate(Tensor waypoints, Tensor state) {
    if (Objects.isNull(waypoints) || Tensors.isEmpty(waypoints))
      throw TensorRuntimeException.of(state, waypoints);
    return locate(waypoints.stream(), state);
  }

  private static int locate(Stream<Tensor> stream, Tensor state) {
    Tensor distances = Tensor.of(stream.map(wp -> Norm._2.ofVector(SE2WRAP.difference(wp, state))));
    return ArgMin.of(distances); // find closest waypoint to current position, exists since waypoints is non-null/-empty
  }

  @Override // from AbstractClockedModule
  protected final Scalar getPeriod() {
    return trajectoryConfig.planningPeriod;
  }

  public List<TrajectorySample> currentTrajectory() {
    return Collections.unmodifiableList(trajectory);
  }

  protected abstract T setupTreePlanner(StateTime root, Tensor goal);

  protected abstract void expandResult(List<TrajectorySample> head, T treePlanner);
}
