// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.List;

import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.Se2MultiresGrids;
import ch.ethz.idsc.gokart.core.slam.SlamDunk;
import ch.ethz.idsc.gokart.core.slam.SlamResult;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.N;

/** localization that uses lidar in combination with gyro rate to rectify measurements
 * 
 * https://github.com/idsc-frazzoli/retina/files/1801718/20180221_2nd_gen_localization.pdf */
public class LidarGyroOfflineLocalize extends OfflineLocalize {
  private static final Tensor MODEL2PIXEL_INITIAL = LocalizationConfig.GLOBAL.getPredefinedMap().getModel2Pixel();
  private static final int MIN_POINTS = LocalizationConfig.GLOBAL.min_points.number().intValue();
  // ---
  /** 3x3 transformation matrix of lidar to center of rear axle */
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart();
  private final SlamDunk slamDunk;
  private final ScatterImage scatterImage;

  /** @param map_image
   * @param pose {x[m], y[m], angle}
   * @param scatterImage */
  public LidarGyroOfflineLocalize(BufferedImage map_image, Tensor pose, Se2MultiresGrids se2MultiresGrids, ScatterImage scatterImage) {
    super(map_image, pose);
    this.slamDunk = new SlamDunk(se2MultiresGrids, slamScore);
    this.scatterImage = scatterImage;
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    Scalar rate = getGyroAndReset().divide(SensorsConfig.GLOBAL.vlp16_rate);
    List<Tensor> list = LocalizationConfig.GLOBAL.getResample() //
        .apply(points).getPointsSpin(SensorsConfig.GLOBAL.vlp16_relativeZero, rate);
    Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
    int sum = scattered.length(); // usually around 430
    if (MIN_POINTS < sum) {
      GeometricLayer geometricLayer = GeometricLayer.of(MODEL2PIXEL_INITIAL);
      Tensor rotate = Se2Utils.toSE2Matrix(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, rate));
      model = model.dot(rotate);
      geometricLayer.pushMatrix(model);
      geometricLayer.pushMatrix(lidar);
      Timing timing = Timing.started();
      SlamResult slamResult = slamDunk.evaluate(geometricLayer, scattered);
      double duration = timing.seconds(); // typical is 0.03
      Tensor pre_delta = slamResult.getTransform();
      Tensor poseDelta = lidar.dot(pre_delta).dot(Inverse.of(lidar));
      // Tensor dstate = Se2Utils.fromSE2Matrix(poseDelta);
      model = model.dot(poseDelta); // advance gokart
      Scalar ratio = N.DOUBLE.apply(slamResult.getQuality());
      appendRow(ratio, sum, duration);
      scatterImage.render(model.dot(lidar), scattered);
    } else {
      System.err.println("few points " + sum);
      skip();
    }
  }
}