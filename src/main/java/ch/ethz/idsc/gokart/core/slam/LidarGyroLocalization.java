// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.top.ImageScore;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;

/** localization algorithm described in
 * https://github.com/idsc-frazzoli/retina/files/1801718/20180221_2nd_gen_localization.pdf */
public class LidarGyroLocalization implements DavisImuFrameListener {
  /** 3x3 transformation matrix of lidar to center of rear axle */
  protected static final Tensor LIDAR = SensorsConfig.GLOBAL.vlp16Gokart();
  private Scalar lidarRate = SensorsConfig.GLOBAL.vlp16_rate;
  private static final Scalar ZERO_RATE = Quantity.of(0, SI.ANGULAR_RATE);
  /** the imu sampling rate is 1000[Hz], the vlp16 revolution rate is configured to 20[Hz]
   * that means per lidar scan there are 50 samples from the imu */
  private static final int SAMPLES = 50; // TODO couple const to 1000 Hz of imu & lidarRate
  // ---
  // private final PredefinedMap predefinedMap;
  private final Tensor model2pixel;
  private Tensor _model = null;
  private final Tensor gyro_y = Array.of(l -> ZERO_RATE, SAMPLES);
  private int gyro_index = 0;
  private final SlamScore slamScore;
  public static final int MIN_POINTS = 250; // TODO magic const in config

  public LidarGyroLocalization(PredefinedMap predefinedMap) {
    // this.predefinedMap = predefinedMap;
    model2pixel = predefinedMap.getModel2Pixel();
    slamScore = ImageScore.of(predefinedMap.getImageExtruded());
  }

  /** @param state {x[m], y[m], angle} */
  public void setState(Tensor state) {
    _model = GokartPoseHelper.toSE2Matrix(state);
  }

  public Optional<SlamResult> handle(Tensor points) {
    Tensor model = _model;
    Scalar rate = getGyroAndReset().divide(lidarRate);
    // System.out.println("rate=" + rate);
    List<Tensor> list = LocalizationConfig.GLOBAL.getUniformResample() //
        .apply(points).getPointsSpin(rate); // TODO optimize
    Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
    int sum = scattered.length(); // usually around 430
    if (LidarGyroLocalization.MIN_POINTS < sum) {
      GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
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
    return Mean.of(gyro_y).Get().multiply(SensorsConfig.GLOBAL.davis_imuY_scale);
  }
}
