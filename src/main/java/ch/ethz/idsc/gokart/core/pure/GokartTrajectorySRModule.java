// code by ynager and jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.mod.PlannerPublish;
import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.se2.glc.SimpleShadowConstraintCV;
import ch.ethz.idsc.owl.bot.tse2.Tse2CarFlows;
import ch.ethz.idsc.owl.bot.tse2.Tse2ComboRegion;
import ch.ethz.idsc.owl.bot.tse2.Tse2ForwardMinTimeGoalManager;
import ch.ethz.idsc.owl.bot.tse2.Tse2Integrator;
import ch.ethz.idsc.owl.bot.tse2.Tse2Wrap;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.MultiConstraintAdapter;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.mapping.ShadowMapSpherical;
import ch.ethz.idsc.owl.math.MinMax;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** rapid prototype implementation to run experiment as part of ICRA publication */
// TODO make configurable as parameter
public class GokartTrajectorySRModule extends AbstractClockedModule {
  public static final Scalar MAX_SPEED = RealScalar.of(8); // 8
  public static final int INIT_VEL = 6;
  static final Scalar MAX_TURNING_PLAN = Degree.of(20); // 45
  static final Tensor ACCELERATIONS = Tensors.vector(-2, 0, 2);
  static final int FLOWRES = 9;
  static final float CAR_RAD = 1.1f; // [m]
  // ---
  // TODO define goal in a generic manner?
  static final Tensor GOAL = Tensors.of( //
      RealScalar.of(54), //
      RealScalar.of(57), //
      DoubleScalar.of(Math.PI / 4), //
      MAX_SPEED.divide(RealScalar.of(2)));
  // ---
  static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-180), Degree.of(180), 72), Subdivide.of(0, 40, 120));
  // ---
  private static final VehicleModel STANDARD = RimoSinusIonModel.standard();
  private static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal(), RealScalar.of(10)).unmodifiable();
  static final FixedStateIntegrator FIXEDSTATEINTEGRATOR = FixedStateIntegrator.create( //
      new Tse2Integrator(Clip.function(MAX_SPEED.zero(), MAX_SPEED)), RationalScalar.of(1, 15), 3);
  // private static final Se2Wrap SE2WRAP = Se2Wrap.INSTANCE;
  private static final StateTimeRaster STATE_TIME_RASTER = //
      new EtaRaster(PARTITIONSCALE, StateTimeTensorFunction.state(Tse2Wrap.INSTANCE::represent));
  private static final int MAX_STEPS = 10000;
  // ---
  static final FlowsInterface TSE2_CARFLOWS = Tse2CarFlows.of( //
      Magnitude.PER_METER.apply(TrajectoryConfig.GLOBAL.maxRotation), ACCELERATIONS);
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final ManualControlProvider joystickLcmProvider = ManualConfig.GLOBAL.createProvider();
  private final Tse2CurvePurePursuitModule tse2CurvePurePursuitModule = //
      new Tse2CurvePurePursuitModule(PursuitConfig.GLOBAL);
  private GokartPoseEvent gokartPoseEvent = null;
  private List<TrajectorySample> trajectory = null;
  private final List<PlannerConstraint> constraints = new ArrayList<>();
  private PlannerConstraint carConstraint;
  private final Collection<CostFunction> extraCosts = new LinkedList<>();
  private final Tensor goalRadius;
  private final Collection<Flow> controls;
  private final GokartPoseListener gokartPoseListener = new GokartPoseListener() {
    @Override
    public void getEvent(GokartPoseEvent getEvent) { // arrives at 50[Hz]
      gokartPoseEvent = getEvent;
    }
  };

  public GokartTrajectorySRModule() {
    MinMax minMax = MinMax.of(STANDARD.footprint());
    Tensor x_samples = Subdivide.of(minMax.min().get(0), minMax.max().get(0), 2); // {-0.295, 0.7349999999999999, 1.765}
    //
    Tensor imageLid = ResourceData.of("/dubilab/sr/lidar_obs.png").get(Tensor.ALL, Tensor.ALL);
    imageLid = ImageEdges.extrusion(imageLid, 3);
    Tensor range = LocalizationConfig.getPredefinedMapObstacles().range();
    ImageRegion irLid = new ImageRegion(imageLid, range, false);
    // ---
    final Scalar goalRadius_xy = DoubleScalar.of(1.2);
    final Scalar goalRadius_theta = Sqrt.of(RealScalar.of(2)).divide(RealScalar.of(20));
    final Scalar goalRadius_v = MAX_SPEED.divide(RealScalar.of(2));
    this.goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta, goalRadius_v);
    // ---
    int resolution = TrajectoryConfig.GLOBAL.controlResolution.number().intValue();
    controls = TSE2_CARFLOWS.getFlows(resolution);
    // ---
    Tensor imageCar = ResourceData.of("/dubilab/sr/car_obs.png"); // grayscale
    Tensor imagePedLegal = ResourceData.of("/dubilab/sr/ped_obs_legal.png"); // grayscale
    ImageRegion irCar = new ImageRegion(imageCar, range, false);
    ImageRegion irPedLegal = new ImageRegion(imagePedLegal, range, false);
    ImageRegion irPedIllegal = new ImageRegion(imageLid, range, false);
    this.carConstraint = RegionConstraints.timeInvariant(Se2PointsVsRegions.line(x_samples, irCar));
    // ---
    TrajectoryRegionQuery lidarRay = SimpleTrajectoryRegionQuery.timeInvariant(irLid);
    LidarEmulator lidarEmulator = new LidarEmulator( //
        LIDAR_RAYTRACER, () -> new StateTime(Tensors.vector(0, 0, 0), RealScalar.ZERO), lidarRay);
    if (PlanSRConfig.GLOBAL.SR_PED_LEGAL) {
      ShadowMapSpherical smPedLegal = //
          new ShadowMapSpherical(lidarEmulator, irPedLegal, //
              Magnitude.VELOCITY.toFloat(PlanSRConfig.GLOBAL.pedVelocity), //
              Magnitude.METER.toFloat(PlanSRConfig.GLOBAL.pedRadius));
      PlannerConstraint pedLegalConst = new SimpleShadowConstraintCV(smPedLegal, irCar, CAR_RAD, //
          Magnitude.ACCELERATION.toFloat(PlanSRConfig.GLOBAL.maxAccel), //
          Magnitude.SECOND.toFloat(PlanSRConfig.GLOBAL.reactionTime), true);
      constraints.add(pedLegalConst);
    }
    if (PlanSRConfig.GLOBAL.SR_PED_ILLEGAL) {
      ShadowMapSpherical smPedIllegal = //
          new ShadowMapSpherical(lidarEmulator, irPedIllegal, //
              Magnitude.VELOCITY.toFloat(PlanSRConfig.GLOBAL.pedVelocity), //
              Magnitude.METER.toFloat(PlanSRConfig.GLOBAL.pedRadius));
      PlannerConstraint pedIllegalConst = new SimpleShadowConstraintCV(smPedIllegal, irCar, CAR_RAD, //
          Magnitude.ACCELERATION.toFloat(PlanSRConfig.GLOBAL.maxAccel), //
          Magnitude.SECOND.toFloat(PlanSRConfig.GLOBAL.reactionTime), true);
      constraints.add(pedIllegalConst);
    }
  }

  @Override // from AbstractClockedModule
  protected void first() throws Exception {
    // ---
    gokartPoseLcmClient.addListener(gokartPoseListener);
    // ---
    gokartPoseLcmClient.startSubscriptions();
    joystickLcmProvider.start();
    rimoGetLcmClient.startSubscriptions();
    // ---
    tse2CurvePurePursuitModule.launch();
  }

  @Override // from AbstractClockedModule
  protected void last() {
    tse2CurvePurePursuitModule.terminate();
    gokartPoseLcmClient.stopSubscriptions();
    joystickLcmProvider.stop();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    if (Objects.nonNull(gokartPoseEvent)) {
      Tensor xya = GokartPoseHelper.toUnitless(gokartPoseEvent.getPose()).copy();
      xya.append(RealScalar.of(INIT_VEL)); // Zero init velocity
      Optional<ManualControlInterface> optional = joystickLcmProvider.getManualControl();
      boolean isResetPressed = optional.isPresent() && optional.get().isResetPressed();
      // ---
      if (Objects.isNull(trajectory) || isResetPressed) { // exists previous trajectory or reset pressed?
        StateTime stateTime = new StateTime(xya, RealScalar.ZERO);
        List<TrajectorySample> head = Arrays.asList(TrajectorySample.head(stateTime));
        // ---
        // CONSTRAINTS
        constraints.add(carConstraint);
        PlannerConstraint plannerConstraints = MultiConstraintAdapter.of(constraints);
        // SETUP
        Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical(GOAL, goalRadius);
        Tse2ForwardMinTimeGoalManager tse2MinTimeGoalManager = new Tse2ForwardMinTimeGoalManager(tse2ComboRegion, controls);
        GoalInterface goalInterface = MultiCostGoalAdapter.of(tse2MinTimeGoalManager.getGoalInterface(), extraCosts);
        TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
            STATE_TIME_RASTER, FIXEDSTATEINTEGRATOR, controls, plannerConstraints, goalInterface);
        // ---
        // PLANNING
        // System.out.println(stateTime.toInfoString());
        trajectoryPlanner.insertRoot(stateTime);
        GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
        glcExpand.findAny(MAX_STEPS);
        expandResult(head, trajectoryPlanner); // build detailed trajectory and pass to purePursuit
      }
      return;
    }
    tse2CurvePurePursuitModule.setCurve(Optional.empty());
    System.err.println("no curve because no pose");
  }

  @Override
  protected final Scalar getPeriod() {
    return TrajectoryConfig.GLOBAL.planningPeriod;
  }

  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) { // goal reached
      System.out.println("goal reached");
      List<TrajectorySample> tail = //
          GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
      trajectory = Trajectories.glue(head, tail);
      tse2CurvePurePursuitModule.setCurveTse2(trajectory);
      PlannerPublish.publishTrajectory(GokartLcmChannel.TRAJECTORY_XYAVT_STATETIME, trajectory);
    } else {
      // failure to reach goal
      System.err.println("goal not reached");
      tse2CurvePurePursuitModule.setCurve(Optional.empty());
      PlannerPublish.publishTrajectory(GokartLcmChannel.TRAJECTORY_XYAVT_STATETIME, new ArrayList<>());
    }
  }
}
