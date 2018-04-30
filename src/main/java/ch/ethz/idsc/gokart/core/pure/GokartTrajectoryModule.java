// code by ynager and jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.bot.se2.glc.CarFlows;
import ch.ethz.idsc.owl.bot.se2.glc.CarForwardFlows;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
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
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class GokartTrajectoryModule extends AbstractClockedModule implements //
    GokartPoseListener, GlcPlannerCallback {
  // TODO make configurable as parameter
  private static final Scalar PLANNING_PERIOD = Quantity.of(1, "s");
  private static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  private static final Scalar SPEED = RealScalar.of(2.5);
  /** rotation per meter driven is at least 23[deg/m]
   * 20180429_minimum_turning_radius.pdf */
  static final CarFlows CARFLOWS = new CarForwardFlows(SPEED, Degree.of(23));
  static final FixedStateIntegrator FIXEDSTATEINTEGRATOR = // node interval == 2/5
      FixedStateIntegrator.create(Se2CarIntegrator.INSTANCE, RationalScalar.of(2, 10), 4);
  static final Se2Wrap SE2WRAP = new Se2Wrap(Tensors.vector(1, 1, 2));
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
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
    ImageRegion region = new ImageRegion(tensor, range, false);
    // ---
    waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
    plannerConstraint = new TrajectoryObstacleConstraint(SimpleTrajectoryRegionQuery.timeInvariant(region));
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
      StateTime stateTime = new StateTime(xya, RealScalar.ZERO);
      List<TrajectorySample> head = Arrays.asList(TrajectorySample.head(stateTime));
      ;
      // has prev traj ?
      if (Objects.nonNull(trajectory)) {
        // [yes: find closest point on previous traj+delay... then plan to "best waypoint"]
        Tensor distances = Tensor.of(trajectory.stream().map(st -> SE2WRAP.distance(st.stateTime().state(), xya)));
        int closestIdx = ArgMin.of(distances);
        StateTime closestStateTime = trajectory.get(closestIdx).stateTime();
        head = getTrajectoryUntil(closestStateTime, closestIdx, Magnitude.METER.apply(PLANNING_PERIOD));
      } else {
        // no: plan from current position to "best waypoint")
        head = Arrays.asList(TrajectorySample.head(stateTime));
      }
      // get waypoint index closest from current position to find suitable goal
      Tensor distances = Tensor.of(waypoints.stream().map(wp -> SE2WRAP.distance(wp, xya)));
      int wpIdx = ArgMin.of(distances);
      if (0 <= wpIdx) {
        wpIdx += 4;
        wpIdx %= waypoints.length();
        System.out.println("goal index = " + wpIdx);
        Tensor goal = waypoints.get(wpIdx);
        Collection<Flow> controls = CARFLOWS.getFlows(9); // TODO magic const
        GoalInterface goalInterface = Se2MinTimeGoalManager.create(goal, goalRadius, controls);
        TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
            PARTITIONSCALE, FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface);
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

  private List<TrajectorySample> getTrajectoryUntil(StateTime tail, int tailIdx, Scalar delay) {
    Scalar tail_delay = tail.time().add(delay);
    if (Objects.isNull(trajectory))
      return Collections.singletonList(TrajectorySample.head(new StateTime(tail.state(), tail_delay)));
    return trajectory.stream() //
        .skip(tailIdx) //
        .filter(trajectorySample -> Scalars.lessEquals(trajectorySample.stateTime().time(), tail_delay)) //
        .collect(Collectors.toList());
  }

  @Override
  protected Scalar getPeriod() {
    return PLANNING_PERIOD;
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
      purePursuitModule.setCurve(Optional.of(curve));
      // System.out.println("yey! assigned curve length == " + curve.length());
    } else {
      // failure to reach goal
      purePursuitModule.setCurve(Optional.empty());
    }
  }
}
