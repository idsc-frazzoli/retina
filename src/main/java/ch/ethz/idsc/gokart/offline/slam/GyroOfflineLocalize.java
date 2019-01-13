// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.Se2MultiresGrids;
import ch.ethz.idsc.gokart.core.slam.SlamDunk;
import ch.ethz.idsc.gokart.core.slam.SlamResult;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.gui.top.ViewLcmFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.N;

/** localization that uses lidar in combination with gyro rate to rectify measurements
 * 
 * https://github.com/idsc-frazzoli/retina/files/1801718/20180221_2nd_gen_localization.pdf */
public class GyroOfflineLocalize extends OfflineLocalize {
  private static final Scalar LIDAR_RATE = Quantity.of(20, SI.PER_SECOND);
  private static final int MIN_POINTS = LocalizationConfig.GLOBAL.min_points.number().intValue();
  private static final int FAN = 4;
  // TODO JPH provide constructor with parameters, max speed, max rate, lidar rate, and fan resolution
  private static final Se2MultiresGrids SE2MULTIRESGRIDS = new Se2MultiresGrids( //
      RealScalar.of(0.8 / FAN), //
      Magnitude.ONE.apply(Quantity.of(9.0 / FAN, NonSI.DEGREE_ANGLE)), //
      FAN, //
      4);
  /** 3x3 transformation matrix of lidar to center of rear axle */
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart();
  private final ScatterImage scatterImage;

  /** @param map_image
   * @param pose {x[m], y[m], angle}
   * @param scatterImage */
  public GyroOfflineLocalize(BufferedImage map_image, Tensor pose, ScatterImage scatterImage) {
    super(map_image, pose);
    this.scatterImage = scatterImage;
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    // TODO the sign of rate was changed 2018-09
    Scalar rate = getGyroAndReset().divide(LIDAR_RATE);
    // System.out.println("rate=" + rate);
    List<Tensor> list = LocalizationConfig.GLOBAL.getResample() //
        .apply(points).getPointsSpin(rate);
    Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
    int sum = scattered.length(); // usually around 430
    if (MIN_POINTS < sum) {
      GeometricLayer geometricLayer = GeometricLayer.of(ViewLcmFrame.MODEL2PIXEL_INITIAL);
      Tensor rotate = Se2Utils.toSE2Matrix(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, rate));
      model = model.dot(rotate);
      geometricLayer.pushMatrix(model);
      geometricLayer.pushMatrix(lidar);
      Timing timing = Timing.started();
      SlamResult slamResult = SlamDunk.of(SE2MULTIRESGRIDS, geometricLayer, scattered, slamScore);
      double duration = timing.seconds(); // typical is 0.03
      Tensor pre_delta = slamResult.getTransform();
      Tensor poseDelta = lidar.dot(pre_delta).dot(Inverse.of(lidar));
      // Tensor dstate = Se2Utils.fromSE2Matrix(poseDelta);
      model = model.dot(poseDelta); // advance gokart
      Scalar ratio = N.DOUBLE.apply(slamResult.getMatchRatio());
      appendRow(ratio, sum, duration);
      scatterImage.render(model.dot(lidar), scattered);
    } else {
      System.err.println("few points " + sum);
      skip();
    }
  }
}