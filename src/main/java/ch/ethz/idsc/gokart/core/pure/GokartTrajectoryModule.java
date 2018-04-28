// code by ynager and jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class GokartTrajectoryModule extends AbstractClockedModule implements //
    GokartPoseListener, GlcPlannerCallback {
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  static final Scalar SPEED = RealScalar.of(2.5);
  static final CarFlows CARFLOWS = new CarForwardFlows(SPEED, Degree.of(15)); // TODO radius
  static final FixedStateIntegrator FIXEDSTATEINTEGRATOR = // node interval == 2/5
      FixedStateIntegrator.create(Se2CarIntegrator.INSTANCE, RationalScalar.of(1, 10), 4);
  static final Se2Wrap SE2WRAP = new Se2Wrap(Tensors.vector(1, 1, 2));
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  // private final PurePursuitModule purePursuitModule = new PurePursuitModule();
  // private // TODO
  GokartPoseEvent gokartPoseEvent = null;
  Tensor obstacleMap;
  Tensor waypoints;
  PlannerConstraint plannerConstraint;
  Tensor goalRadius;

  @Override
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    // purePursuitModule.launch();
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

  @Override
  protected void last() {
    // purePursuitModule.terminate();
    gokartPoseLcmClient.stopSubscriptions();
  }

  private MotionPlanWorker motionPlanWorker;

  @Override
  protected void runAlgo() {
    if (Objects.nonNull(gokartPoseEvent)) {
      if (Objects.nonNull(motionPlanWorker)) {
        motionPlanWorker.flagShutdown();
        motionPlanWorker = null;
      }
      System.out.println("setup planner");
      final Tensor xya = GokartPoseHelper.toUnitless(gokartPoseEvent.getPose()).unmodifiable();
      StateTime stateTime = new StateTime(xya, RealScalar.ZERO);
      // has prev traj ?
      // no: plan from current to "best waypoint")
      Tensor distances = Tensor.of(waypoints.stream().map(wp -> SE2WRAP.distance(wp, xya)));
      int index = ArgMin.of(distances);
      if (0 <= index) {
        index += 4;
        index %= waypoints.length();
        System.out.println("goal index = " + index);
        Tensor goal = waypoints.get(index);
        Collection<Flow> controls = CARFLOWS.getFlows(9); // TODO magic const
        GoalInterface goalInterface = Se2MinTimeGoalManager.create(goal, goalRadius, controls);
        TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
            PARTITIONSCALE, FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface);
        trajectoryPlanner.represent = StateTimeTensorFunction.state(SE2WRAP::represent);
        motionPlanWorker = new MotionPlanWorker();
        motionPlanWorker.addCallback(this);
        TrajectorySample trajectorySample = TrajectorySample.head(stateTime);
        motionPlanWorker.start(Arrays.asList(trajectorySample), trajectoryPlanner);
        System.out.println("started");
        // [yes: find closest point on previous traj+delayHint... then plan to "best waypoint"]
        // in call back set curve
        return;
      }
    }
    // no pose -> no traj
    // set curve to optional.empty
    PurePursuitModule.PPM.setCurve(Optional.empty());
    System.err.println("no curve because no pose");
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(0.5, "s"); // TODO make configurable as parameter
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) { // arrives at 50[Hz]
    this.gokartPoseEvent = gokartPoseEvent;
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<TrajectorySample> tail = //
          GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
      // set curve
      Tensor curve = Tensor.of(tail.stream().map(ts -> ts.stateTime().state().extract(0, 2)));
      PurePursuitModule.PPM.setCurve(Optional.of(curve));
      System.out.println("yey! assigned curve length == " + curve.length());
    } else {
      PurePursuitModule.PPM.setCurve(Optional.empty());
      System.err.println("no curve");
    }
  }
}
