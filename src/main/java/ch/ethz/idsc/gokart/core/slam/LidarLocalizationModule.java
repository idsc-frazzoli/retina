// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmServer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** match the most recent lidar scan to static geometry of a pre-recorded map
 * the module runs a separate thread. on a standard pc the matching takes 0.017[s] on average */
public class LidarLocalizationModule extends AbstractModule implements LidarRayBlockListener, Runnable {
  // TODO JAN bad design
  public static boolean TRACKING = false;
  public static boolean FLAGSNAP = false;
  // ---
  private final GokartPoseOdometry gokartPoseOdometry = //
      GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry();
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  // private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  public final LidarGyroLocalization lidarGyroLocalization = LocalizationConfig.getLidarGyroLocalization();
  /** tear down flag to stop thread */
  private boolean isLaunched = true;
  private final Thread thread = new Thread(this);
  /** points_ferry is null or a matrix with dimension Nx2
   * containing the cross-section of the static geometry
   * with the horizontal plane at height of the lidar */
  private Tensor points2d_ferry = null;

  @Override // from AbstractModule
  protected void first() throws Exception {
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = LocalizationConfig.GLOBAL.planarEmulatorVlp16();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    lidarAngularFiringCollector.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
    // davisImuLcmClient.addListener(lidarGyroLocalization);
    // ---
    vlp16LcmHandler.startSubscriptions();
    // davisImuLcmClient.startSubscriptions();
    thread.start();
  }

  @Override // from AbstractModule
  protected void last() {
    isLaunched = false;
    thread.interrupt();
    vlp16LcmHandler.stopSubscriptions();
    // davisImuLcmClient.stopSubscriptions();
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) { // receive 2D block event
    if (FLAGSNAP || TRACKING) {
      FLAGSNAP = false;
      FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
      points2d_ferry = Tensors.vector(i -> Tensors.of( //
          DoubleScalar.of(floatBuffer.get()), //
          DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
      thread.interrupt();
    }
  }

  @Override // from Runnable
  public void run() {
    while (isLaunched) {
      Tensor points = points2d_ferry;
      if (Objects.nonNull(points)) {
        points2d_ferry = null;
        Tensor state = gokartPoseOdometry.getPose(); // {x[m],y[m],angle[]}
        // System.out.println("tracking");
        lidarGyroLocalization.setState(state);
        // Stopwatch stopwatch = Stopwatch.started();
        Optional<SlamResult> optional = lidarGyroLocalization.handle(points);
        // double duration = stopwatch.display_seconds();
        if (optional.isPresent()) {
          SlamResult slamResult = optional.get();
          // OUT={37.85[m], 38.89[m], -0.5658221}
          gokartPoseOdometry.setPose(slamResult.getTransform(), slamResult.getMatchRatio());
        } else
          // TODO check is the code below is sufficient
          gokartPoseOdometry.setPose(state, RealScalar.ZERO);
      } else
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // ---
        }
    }
  }
}
