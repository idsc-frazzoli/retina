// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnListener;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.autobox.SteerColumnLcmClient;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmClient;
import ch.ethz.idsc.retina.app.clear.ClearanceTracker;
import ch.ethz.idsc.retina.app.clear.ObstructedClearanceTracker;
import ch.ethz.idsc.retina.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.LidarXYZEvent;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SpacialProvider;
import ch.ethz.idsc.retina.util.data.SoftWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Min;

/** Important: the module requires the steering to be calibrated.
 * 
 * prevents acceleration if something is in the way
 * for instance when a person is entering or leaving the gokart */
abstract class Vlp16ClearanceModule extends EmergencyModule<RimoPutEvent> implements //
    LidarSpacialListener, SteerColumnListener {
  private static final Scalar UNIT_SPEED = DoubleScalar.of(1);
  private static final double PENALTY_DURATION_S = 0.5;
  // ---
  private final VelodyneLcmClient velodyneLcmClient;
  private final LidarSpacialProvider lidarSpacialProvider;
  private final SteerColumnLcmClient steerColumnLcmClient = new SteerColumnLcmClient();
  private final SpacialXZObstaclePredicate spacialXZObstaclePredicate = //
      SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final Watchdog watchdog = SoftWatchdog.barking(PENALTY_DURATION_S);
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();

  public Vlp16ClearanceModule() {
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    velodyneLcmClient = new VelodyneLcmClient(VelodyneModel.VLP16, velodyneDecoder, GokartLcmChannel.VLP16_CENTER);
    lidarSpacialProvider = new Vlp16SpacialProvider(SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue());
    velodyneDecoder.addRayListener(lidarSpacialProvider);
  }

  @Override // from AbstractModule
  protected final void first() {
    lidarSpacialProvider.addListener(this);
    velodyneLcmClient.startSubscriptions();
    steerColumnLcmClient.addListener(this);
    steerColumnLcmClient.startSubscriptions();
    protected_first();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected final void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    protected_last();
    velodyneLcmClient.stopSubscriptions();
    steerColumnLcmClient.stopSubscriptions();
  }

  void protected_first() {
    // ---
  }

  void protected_last() {
    // ---
  }

  /***************************************************/
  /** clearanceTracker is always non-null */
  private ClearanceTracker clearanceTracker = new ObstructedClearanceTracker(RealScalar.of(1));
  /** synchronized read/write access */
  private Optional<Scalar> contact = Optional.empty();

  @Override // from LidarSpacialListener
  public final void lidarSpacial(LidarXYZEvent lidarXYZEvent) {
    ClearanceTracker _clearanceTracker = clearanceTracker;
    float x = lidarXYZEvent.coords[0];
    float z = lidarXYZEvent.coords[2];
    if (spacialXZObstaclePredicate.isObstacle(x, z) && //
        _clearanceTracker.isObstructed(lidarXYZEvent.getXY()))
      watchdog.notifyWatchdog();
  }

  @Override // from GokartStatusListener
  public final void getEvent(SteerColumnEvent steerColumnEvent) {
    Optional<Scalar> touching = clearanceTracker.contact();
    if (touching.isPresent()) {
      Optional<Scalar> _contact = contact;
      contact = _contact.isPresent() //
          ? Optional.of(Min.of(_contact.get(), touching.get())) //
          : touching;
    }
    clearanceTracker = steerColumnEvent.isSteerColumnCalibrated() //
        ? SafetyConfig.GLOBAL.getClearanceTracker( //
            UNIT_SPEED, //
            Magnitude.PER_METER.apply(steerMapping.getRatioFromSCE(steerColumnEvent)))
        : new ObstructedClearanceTracker(RealScalar.of(1));
  }

  @Override // from RimoPutProvider
  public final Optional<RimoPutEvent> putEvent() {
    Optional<Scalar> _contact = contact;
    if (_contact.isPresent()) {
      EmergencyBrakeProvider.INSTANCE.consider(_contact.get());
      contact = Optional.empty();
    }
    return watchdog.isBarking() //
        ? Optional.empty()
        : penaltyAction();
  }

  /** @return non-null */
  abstract Optional<RimoPutEvent> penaltyAction();
}
