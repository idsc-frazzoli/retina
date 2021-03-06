// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

/** matches the most recent lidar scan to static geometry of a pre-recorded map.
 * on a standard pc the matching takes 0.017[s] on average.
 * 
 * the localization algorithm is described in
 * https://github.com/idsc-frazzoli/retina/files/1801718/20180221_2nd_gen_localization.pdf */
public class LidarGyroLocalization {
  /** @param localizationConfig
   * @return */
  public static LidarGyroLocalization of(LocalizationConfig localizationConfig) {
    PredefinedMap predefinedMap = localizationConfig.getPredefinedMap();
    SlamDunk slamDunk = new SlamDunk( //
        localizationConfig.createSe2MultiresGrids(), //
        ImageScore.of(predefinedMap.getImageExtruded()));
    return new LidarGyroLocalization( //
        localizationConfig.min_points.number().intValue(), //
        predefinedMap.getModel2Pixel(), //
        slamDunk);
  }

  // ---
  private final int min_points;
  /** 3x3 transformation matrix of lidar to center of rear axle */
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart();
  private final Tensor inverseLidar = Inverse.of(lidar).unmodifiable();
  /** lidar rate has unit s^-1 */
  private final Scalar lidarRate = SensorsConfig.GLOBAL.vlp16_rate;
  // ---
  private final Tensor model2pixel;
  private final SlamDunk slamDunk;

  public LidarGyroLocalization(int min_points, Tensor model2pixel, SlamDunk slamDunk) {
    this.min_points = min_points;
    this.model2pixel = model2pixel;
    this.slamDunk = slamDunk;
  }

  /** @param pose {x[m], y[m], angle}
   * @param velocity {vx[m*s^-1], vy[m*s^-1], gyroZ[s^-1]} of which only the last entry is considered
   * @param points
   * @return */
  public Optional<GokartPoseEvent> handle(Tensor pose, Tensor velocity, Tensor points) {
    Tensor model = PoseHelper.toSE2Matrix(pose);
    Tensor rate = velocity.divide(lidarRate);
    List<Tensor> list = LocalizationConfig.GLOBAL.getResample() //
        .apply(points).getPointsSpin(SensorsConfig.GLOBAL.vlp16_relativeZero, rate.Get(2));
    Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
    int sum = scattered.length(); // usually around 430
    if (min_points < sum) {
      GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
      geometricLayer.pushMatrix(model);
      geometricLayer.pushMatrix(lidar);
      SlamResult slamResult = slamDunk.evaluate(geometricLayer, scattered); // 0.03[s]
      Tensor pre_delta = slamResult.getTransform();
      Tensor poseDelta = lidar.dot(pre_delta).dot(inverseLidar);
      model = model.dot(poseDelta); // advance gokart
      Tensor result = Se2Matrix.toVector(model);
      return Optional.of(GokartPoseEvents.offlineV1( //
          PoseHelper.attachUnits(result), //
          slamResult.getQuality()));
    }
    System.err.println("few points " + sum);
    return Optional.empty();
  }
}
