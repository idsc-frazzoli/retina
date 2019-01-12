// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.fuse.DavisImuTracker;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.top.ImageScore;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Inverse;

/** localization algorithm described in
 * https://github.com/idsc-frazzoli/retina/files/1801718/20180221_2nd_gen_localization.pdf */
public class LidarGyroLocalization {
  private static final Se2MultiresGrids SE2_MULTIRES_GRIDS = LocalizationConfig.GLOBAL.createSe2MultiresGrids();
  // ---
  private final int min_points = LocalizationConfig.GLOBAL.min_points.number().intValue();
  /** 3x3 transformation matrix of lidar to center of rear axle */
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart();
  private final Tensor inverseLidar = Inverse.of(lidar).unmodifiable();
  /** lidar rate has unit s^-1 */
  private final Scalar lidarRate = SensorsConfig.GLOBAL.vlp16_rate;
  // ---
  private final Tensor model2pixel;
  private final SlamScore slamScore;
  private Tensor _model = null;

  public LidarGyroLocalization(PredefinedMap predefinedMap) {
    model2pixel = predefinedMap.getModel2Pixel();
    slamScore = ImageScore.of(predefinedMap.getImageExtruded());
  }

  /** @param state {x[m], y[m], angle} */
  void setState(Tensor state) {
    _model = GokartPoseHelper.toSE2Matrix(state);
  }

  /** call {@link #setState(Tensor)} before invoking {@link #handle(Tensor)}
   * 
   * @param points
   * @return */
  Optional<SlamResult> handle(Tensor points) {
    Tensor model = _model;
    Scalar rate = DavisImuTracker.INSTANCE.getGyroZ().divide(lidarRate);
    // System.out.println("rate=" + rate);
    List<Tensor> list = LocalizationConfig.GLOBAL.getResample() //
        .apply(points).getPointsSpin(rate); // TODO optimize
    Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
    int sum = scattered.length(); // usually around 430
    if (min_points < sum) {
      GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
      Tensor rotate = Se2Utils.toSE2Matrix(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, rate));
      model = model.dot(rotate);
      geometricLayer.pushMatrix(model);
      geometricLayer.pushMatrix(lidar);
      // Stopwatch stopwatch = Stopwatch.started();
      SlamResult slamResult = SlamDunk.of(SE2_MULTIRES_GRIDS, geometricLayer, scattered, slamScore);
      // double duration = stopwatch.display_seconds(); // typical is 0.03
      Tensor pre_delta = slamResult.getTransform();
      Tensor poseDelta = lidar.dot(pre_delta).dot(inverseLidar);
      model = model.dot(poseDelta); // advance gokart
      Tensor result = Se2Utils.fromSE2Matrix(model);
      Tensor vector = GokartPoseHelper.attachUnits(result);
      // TODO bad style to repurpose SlamResult
      return Optional.of(new SlamResult(vector, slamResult.getMatchRatio()));
    }
    System.err.println("few points " + sum);
    return Optional.empty();
  }
}
