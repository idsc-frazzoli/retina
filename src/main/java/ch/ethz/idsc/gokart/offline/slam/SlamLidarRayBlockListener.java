// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.slam.DubendorfSlam;
import ch.ethz.idsc.gokart.slam.SlamDunk;
import ch.ethz.idsc.gokart.slam.SlamResult;
import ch.ethz.idsc.gokart.slam.SlamScore;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.zhkart.pos.LocalizationConfig;
import ch.ethz.idsc.retina.gui.gokart.top.ImageScore;
import ch.ethz.idsc.retina.gui.gokart.top.ResampledLidarRender;
import ch.ethz.idsc.retina.gui.gokart.top.StoreMapUtil;
import ch.ethz.idsc.retina.gui.gokart.top.ViewLcmFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Round;

/** the test matches 3 consecutive lidar scans to the dubendorf hangar map
 * the matching qualities are 51255, 43605, 44115 */
public class SlamLidarRayBlockListener extends OfflineLocalize {
  static final boolean EXPORT = false;
  // ---
  BufferedImage map_image = StoreMapUtil.loadOrNull();
  int count = 0;

  public SlamLidarRayBlockListener(Tensor model) {
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
      System.out.println(time.map(Magnitude.SECOND).map(Round._2));
      {
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
        tableBuilder.appendRow(time.map(Magnitude.SECOND), //
            Se2Utils.fromSE2Matrix(model), //
            dstate, //
            N.DOUBLE.apply(slamResult.getMatchRatio()), //
            RealScalar.of(sum), //
            RealScalar.of(duration));
        int quality = slamResult.getMatchRatio().multiply(RealScalar.of(sum * 255)).number().intValue();
      }
      // ---
      if (EXPORT) {
        BufferedImage image = new BufferedImage(map_image.getWidth(), map_image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = image.createGraphics();
        graphics2d.drawImage(map_image, 0, 0, null);
        GeometricLayer geometricLayer = new GeometricLayer(ViewLcmFrame.MODEL2PIXEL_INITIAL, Array.zeros(3));
        geometricLayer.pushMatrix(model);
        geometricLayer.pushMatrix(LIDAR);
        graphics2d.setColor(Color.GREEN);
        for (Tensor x : scattered) {
          Point2D p = geometricLayer.toPoint2D(x);
          graphics2d.fillRect((int) p.getX(), (int) p.getY(), 2, 2);
        }
        graphics2d.setColor(Color.GRAY);
        {
          Point2D p0 = geometricLayer.toPoint2D(Tensors.vector(0, 0));
          Point2D pX = geometricLayer.toPoint2D(Tensors.vector(10, 0));
          Point2D pY = geometricLayer.toPoint2D(Tensors.vector(0, 10));
          graphics2d.draw(new Line2D.Double(p0, pX));
          graphics2d.draw(new Line2D.Double(p0, pY));
        }
        // graphics2d.drawString("q=" + quality, 0, 10);
        try {
          ImageIO.write(image, "png", UserHome.Pictures("slam" + count + ".png"));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      ++count;
    } else
      skipped.append(time.map(Magnitude.SECOND));
  }
}