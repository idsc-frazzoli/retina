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
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.N;

/** localization using only lidar */
public class SlamOfflineLocalize extends OfflineLocalize {
  private static final Se2MultiresGrids SE2MULTIRESGRIDS = LocalizationConfig.GLOBAL.createSe2MultiresGrids();
  // ---
  private final int min_points = LocalizationConfig.GLOBAL.min_points.number().intValue();
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart();
  private final ScatterImage scatterImage;

  /** @param map_image
   * @param pose {x[m], y[m], angle}
   * @param scatterImage */
  public SlamOfflineLocalize(BufferedImage map_image, Tensor pose, ScatterImage scatterImage) {
    super(map_image, pose);
    this.scatterImage = scatterImage;
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    List<Tensor> list = LocalizationConfig.GLOBAL.getResample().apply(points).getPoints();
    Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
    int sum = scattered.length(); // usually around 430
    if (min_points < sum) {
      GeometricLayer geometricLayer = GeometricLayer.of(ViewLcmFrame.MODEL2PIXEL_INITIAL);
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
    } else
      skip();
  }
}