// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.owl.car.math.ClearanceTracker;
import ch.ethz.idsc.owl.car.math.EmptyClearanceTracker;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16SpacialLcmHandler;
import ch.ethz.idsc.retina.sys.SafetyCritical;
import ch.ethz.idsc.retina.util.data.PenaltyTimeout;
import ch.ethz.idsc.tensor.Scalar;

/** Important: the module requires the steering to be calibrated.
 * 
 * prevents acceleration if something is in the way
 * for instance when a person is entering or leaving the gokart */
@SafetyCritical
abstract class Vlp16ClearanceModule extends EmergencyModule<RimoPutEvent> implements //
    LidarSpacialListener, GokartStatusListener {
  private static final double PENALTY_DURATION_S = 0.5;
  // ---
  private final Vlp16SpacialLcmHandler vlp16SpacialLcmHandler = SensorsConfig.GLOBAL.vlp16SpacialLcmHandler();
  // TODO later use steerColumnTracker directly
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private final SpacialXZObstaclePredicate spacialXZObstaclePredicate //
      = SafetyConfig.GLOBAL.createVlp16();
  private final PenaltyTimeout penaltyTimeout = new PenaltyTimeout(PENALTY_DURATION_S);
  /** clearanceTracker is always non-null */
  private ClearanceTracker clearanceTracker = EmptyClearanceTracker.INSTANCE;

  @Override // from AbstractModule
  protected final void first() throws Exception {
    vlp16SpacialLcmHandler.lidarSpacialProvider.addListener(this);
    vlp16SpacialLcmHandler.startSubscriptions();
    gokartStatusLcmClient.addListener(this);
    gokartStatusLcmClient.startSubscriptions();
    protected_first();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected final void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    protected_last();
    vlp16SpacialLcmHandler.stopSubscriptions();
    gokartStatusLcmClient.stopSubscriptions();
  }

  void protected_first() {
    // ---
  }

  void protected_last() {
    // ---
  }

  /***************************************************/
  @Override // from LidarSpacialListener
  public final void lidarSpacial(LidarSpacialEvent lidarSpacialEvent) {
    ClearanceTracker _clearanceTracker = clearanceTracker;
    float x = lidarSpacialEvent.coords[0];
    float z = lidarSpacialEvent.coords[2];
    if (spacialXZObstaclePredicate.isObstacle(x, z) && //
        _clearanceTracker.isObstructed(lidarSpacialEvent.getXY()))
      penaltyTimeout.flagPenalty();
  }

  @Override // from GokartStatusListener
  public final void getEvent(GokartStatusEvent gokartStatusEvent) {
    clearanceTracker = SafetyConfig.GLOBAL.getClearanceTracker(gokartStatusEvent);
  }

  @Override // from RimoPutProvider
  public final Optional<RimoPutEvent> putEvent() {
    Optional<Scalar> contact = clearanceTracker.contact();
    if (contact.isPresent())
      EmergencyBrakeProvider.INSTANCE.consider(contact.get());
    return Optional.ofNullable(penaltyTimeout.isPenalty() ? penaltyAction() : null);
  }

  /** @return non-null */
  abstract RimoPutEvent penaltyAction();
}
