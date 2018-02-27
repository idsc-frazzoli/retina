// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.nio.FloatBuffer;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.top.ImageScore;
import ch.ethz.idsc.gokart.gui.top.PredefinedMap;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.gui.top.ViewLcmFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;

public class LidarGyroLocalization implements LidarRayBlockListener, DavisImuFrameListener {
  /** 3x3 transformation matrix of lidar to center of rear axle */
  protected static final Tensor LIDAR = Se2Utils.toSE2Matrix(SensorsConfig.GLOBAL.vlp16).unmodifiable();
  private static final Scalar LIDAR_RATE = Quantity.of(20, "s^-1");
  private static final Scalar ZERO_RATE = Quantity.of(0, SI.ANGULAR_RATE);
  // ---
  private Tensor model; // FIXME
  private Tensor gyro_y = Tensors.empty();
  private final SlamScore slamScore;
  public static final int MIN_POINTS = 250;

  public LidarGyroLocalization(PredefinedMap predefinedMap) {
    slamScore = ImageScore.of(predefinedMap.getImageExtruded());
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    handle(points);
  }

  public void handle(Tensor points) {
    Scalar rate = getGyroAndReset().divide(LIDAR_RATE);
    // System.out.println("rate=" + rate);
    List<Tensor> list = LocalizationConfig.GLOBAL.getUniformResample() //
        .apply(points).getPointsSpin(rate); // TODO optimize
    Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
    int sum = scattered.length(); // usually around 430
    if (LidarGyroLocalization.MIN_POINTS < sum) {
      GeometricLayer geometricLayer = new GeometricLayer(ViewLcmFrame.MODEL2PIXEL_INITIAL, Array.zeros(3));
      Tensor rotate = Se2Utils.toSE2Matrix(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, rate));
      model = model.dot(rotate);
      geometricLayer.pushMatrix(model);
      geometricLayer.pushMatrix(LIDAR);
      // Stopwatch stopwatch = Stopwatch.started();
      SlamResult slamResult = SlamDunk.of(DubendorfSlam.SE2MULTIRESGRIDS, geometricLayer, scattered, slamScore);
      // double duration = stopwatch.display_seconds(); // typical is 0.03
      Tensor pre_delta = slamResult.getTransform();
      Tensor poseDelta = LIDAR.dot(pre_delta).dot(Inverse.of(LIDAR));
      // Tensor dstate = Se2Utils.fromSE2Matrix(poseDelta);
      model = model.dot(poseDelta); // advance gokart
    } else {
      System.err.println("few points " + sum);
    }
  }

  @Override // from DavisImuFrameListener
  public void imuFrame(DavisImuFrame davisImuFrame) {
    Scalar rate = davisImuFrame.gyroImageFrame().Get(1); // image - y axis
    gyro_y.append(rate);
  }

  protected final Scalar getGyroAndReset() {
    Scalar mean = Tensors.isEmpty(gyro_y) ? ZERO_RATE : Mean.of(gyro_y).Get();
    gyro_y = Tensors.empty();
    return mean;
  }
}
