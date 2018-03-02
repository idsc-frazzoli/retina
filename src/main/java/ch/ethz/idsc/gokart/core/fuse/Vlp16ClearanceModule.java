// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16SpacialLcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.data.PenaltyTimeout;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** prevents acceleration if something is in the way
 * for instance when a person is entering or leaving the gokart */
public final class Vlp16ClearanceModule extends AbstractModule implements //
    LidarSpacialListener, RimoPutProvider, GokartStatusListener {
  private static final double PENALTY_DURATION_S = 0.5;
  // ---
  private final Vlp16SpacialLcmHandler vlp16SpacialLcmHandler = //
      new Vlp16SpacialLcmHandler(GokartLcmChannel.VLP16_CENTER);
  // TODO later use steerColumnTracker directly
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private ClearanceTracker clearanceTracker;
  private final PenaltyTimeout penaltyTimeout = new PenaltyTimeout(PENALTY_DURATION_S);
  private final float vlp16_ZLo;
  private final float vlp16_ZHi;

  public Vlp16ClearanceModule() {
    vlp16_ZLo = SafetyConfig.GLOBAL.vlp16_ZLoMeter().number().floatValue();
    vlp16_ZHi = SafetyConfig.GLOBAL.vlp16_ZHiMeter().number().floatValue();
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    vlp16SpacialLcmHandler.lidarSpacialProvider.addListener(this);
    vlp16SpacialLcmHandler.startSubscriptions();
    gokartStatusLcmClient.addListener(this);
    gokartStatusLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    vlp16SpacialLcmHandler.stopSubscriptions();
    gokartStatusLcmClient.stopSubscriptions();
  }

  @Override // from LidarSpacialListener
  public void lidarSpacial(LidarSpacialEvent lidarSpacialEvent) {
    float z = lidarSpacialEvent.coords[2];
    ClearanceTracker _clearanceTracker = clearanceTracker;
    if (vlp16_ZLo < z && z < vlp16_ZHi && Objects.nonNull(_clearanceTracker)) {
      Tensor local = Tensors.vectorDouble( //
          lidarSpacialEvent.coords[0], //
          lidarSpacialEvent.coords[1]);
      if (_clearanceTracker.probe(local))
        penaltyTimeout.flagPenalty();
    }
  }

  @Override // from GokartStatusListener
  public void getEvent(GokartStatusEvent gokartStatusEvent) {
    if (gokartStatusEvent.isSteerColumnCalibrated()) {
      Scalar angle = SteerConfig.GLOBAL.getAngleFromSCE(gokartStatusEvent);
      Scalar half = ChassisGeometry.GLOBAL.yHalfWidthMeter();
      clearanceTracker = new ClearanceTracker(half, angle, SensorsConfig.GLOBAL.vlp16);
    } else
      clearanceTracker = null;
  }

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    boolean status = false;
    status |= Objects.isNull(clearanceTracker);
    status |= penaltyTimeout.isPenalty();
    return Optional.ofNullable(status ? RimoPutEvent.PASSIVE : null);
  }
}
