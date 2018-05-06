// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.nio.FloatBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmServer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class LidarLocalizationModule extends AbstractModule implements LidarRayBlockListener {
  // TODO bad design
  public static boolean TRACKING = false;
  public static boolean FLAGSNAP = false;
  // ---
  private final GokartPoseOdometry gokartPoseOdometry = //
      GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry();
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
  public final LidarGyroLocalization lidarGyroLocalization = new LidarGyroLocalization(predefinedMap);

  @Override // from AbstractModule
  protected void first() throws Exception {
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = SensorsConfig.GLOBAL.horizontalEmulatorVlp16();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    lidarAngularFiringCollector.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
    davisImuLcmClient.addListener(lidarGyroLocalization);
    // ---
    vlp16LcmHandler.startSubscriptions();
    davisImuLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    vlp16LcmHandler.stopSubscriptions();
    davisImuLcmClient.stopSubscriptions();
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) { // receive 2D block event
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    // Optional<SlamResult> optional = lidarGyroLocalization.handle(points);
    Tensor state = gokartPoseOdometry.getPose(); // {x[m],y[m],angle[]}
    // System.out.println("HERE " + Dimensions.of(points) + " " + state);
    // System.out.println("IN = " + state);
    if (FLAGSNAP || TRACKING) {
      FLAGSNAP = false;
      // System.out.println("tracking");
      lidarGyroLocalization.setState(state);
      // Stopwatch stopwatch = Stopwatch.started();
      Optional<SlamResult> optional = lidarGyroLocalization.handle(points);
      // double duration = stopwatch.display_seconds();
      if (optional.isPresent()) {
        SlamResult slamResult = optional.get();
        // OUT={37.85[m], 38.89[m], -0.5658221}
        gokartPoseOdometry.setPose(slamResult.getTransform(), slamResult.getMatchRatio());
      } else {
        // System.err.println("insufficient: " + sum);
        gokartPoseOdometry.setPose(state, RealScalar.ZERO);
      }
    }
  }
}
