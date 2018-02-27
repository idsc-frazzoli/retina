// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
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
  private Tensor _model = null;
  private final Tensor gyro_y = Array.of(l -> ZERO_RATE, 50); // TODO document magic const
  private int gyro_index = 0;
  private final SlamScore slamScore;
  public static final int MIN_POINTS = 250;

  public LidarGyroLocalization(PredefinedMap predefinedMap) {
    slamScore = ImageScore.of(predefinedMap.getImageExtruded());
  }

  /** @param state {x[m], y[m], angle} */
  public void setState(Tensor state) {
    _model = GokartPoseHelper.toSE2Matrix(state);
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    handle(points);
  }

  public Optional<SlamResult> handle(Tensor points) {
    Tensor model = _model;
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
      model = model.dot(poseDelta); // advance gokart
      Tensor result = Se2Utils.fromSE2Matrix(model);
      Tensor vector = Tensors.of( //
          Quantity.of(result.Get(0), SI.METER), //
          Quantity.of(result.Get(1), SI.METER), //
          result.Get(2));
      // TODO bad style to repurpose SlamResult
      return Optional.of(new SlamResult(vector, slamResult.getMatchRatio()));
    }
    System.err.println("few points " + sum);
    return Optional.empty();
  }

  @Override // from DavisImuFrameListener
  public void imuFrame(DavisImuFrame davisImuFrame) {
    Scalar rate = davisImuFrame.gyroImageFrame().Get(1); // image - y axis
    gyro_y.set(rate, gyro_index);
    ++gyro_index;
    gyro_index %= gyro_y.length();
  }

  protected final Scalar getGyroAndReset() {
    return Mean.of(gyro_y).Get();
  }
}
