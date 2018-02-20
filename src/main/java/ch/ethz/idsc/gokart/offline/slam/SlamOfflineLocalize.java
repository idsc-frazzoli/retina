// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.List;

import ch.ethz.idsc.gokart.slam.DubendorfSlam;
import ch.ethz.idsc.gokart.slam.SlamDunk;
import ch.ethz.idsc.gokart.slam.SlamResult;
import ch.ethz.idsc.gokart.slam.SlamScore;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.zhkart.pos.LocalizationConfig;
import ch.ethz.idsc.retina.gui.gokart.top.ImageScore;
import ch.ethz.idsc.retina.gui.gokart.top.ResampledLidarRender;
import ch.ethz.idsc.retina.gui.gokart.top.ViewLcmFrame;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.N;

/** the test matches 3 consecutive lidar scans to the dubendorf hangar map
 * the matching qualities are 51255, 43605, 44115 */
public class SlamOfflineLocalize extends OfflineLocalize {
  /** @param model */
  public SlamOfflineLocalize(Tensor model) {
    super(model);
    if (map_image.getType() != BufferedImage.TYPE_BYTE_GRAY)
      throw new RuntimeException();
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    List<Tensor> list = LocalizationConfig.GLOBAL.getUniformResample().apply(points).getPoints();
    Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
    int sum = scattered.length(); // usually around 430
    if (ResampledLidarRender.MIN_POINTS < sum) {
      SlamScore slamScore = ImageScore.of(map_image);
      GeometricLayer geometricLayer = new GeometricLayer(ViewLcmFrame.MODEL2PIXEL_INITIAL, Array.zeros(3));
      geometricLayer.pushMatrix(model);
      geometricLayer.pushMatrix(LIDAR);
      Stopwatch stopwatch = Stopwatch.started();
      SlamResult slamResult = SlamDunk.of(DubendorfSlam.SE2MULTIRESGRIDS, geometricLayer, scattered, slamScore);
      double duration = stopwatch.display_seconds(); // typical is 0.03
      Tensor pre_delta = slamResult.getTransform();
      Tensor poseDelta = LIDAR.dot(pre_delta).dot(Inverse.of(LIDAR));
      Tensor dstate = Se2Utils.fromSE2Matrix(poseDelta);
      model = model.dot(poseDelta); // advance gokart
      Scalar ratio = N.DOUBLE.apply(slamResult.getMatchRatio());
      appendRow(dstate, ratio, sum, duration);
      render(scattered);
    } else
      skip();
  }
}