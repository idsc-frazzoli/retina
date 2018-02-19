package ch.ethz.idsc.gokart.offline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.zhkart.pos.LocalizationConfig;
import ch.ethz.idsc.retina.gui.gokart.top.DubendorfSlam;
import ch.ethz.idsc.retina.gui.gokart.top.ImageScore;
import ch.ethz.idsc.retina.gui.gokart.top.ResampledLidarRender;
import ch.ethz.idsc.retina.gui.gokart.top.SlamResult;
import ch.ethz.idsc.retina.gui.gokart.top.SlamScore;
import ch.ethz.idsc.retina.gui.gokart.top.SpinDunk;
import ch.ethz.idsc.retina.gui.gokart.top.StoreMapUtil;
import ch.ethz.idsc.retina.gui.gokart.top.ViewLcmFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.ResampleResult;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

/** the test matches 3 consecutive lidar scans to the dubendorf hangar map
 * the matching qualities are
 * 51255
 * 43605
 * 44115 */
public class SpinLidarRayBlockListener extends OfflineLocalize {
  static final boolean EXPORT = true;
  // ---
  BufferedImage map_image = StoreMapUtil.loadOrNull();
  int skipped = 0;
  int count = 0;

  public SpinLidarRayBlockListener(Tensor model) {
    super(model);
    TestCase.assertEquals(map_image.getType(), BufferedImage.TYPE_BYTE_GRAY);
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    TestCase.assertFalse(floatBuffer.hasRemaining());
    // System.out.println(lidarRayBlockEvent.size());
    ResampleResult resampleResult = LocalizationConfig.GLOBAL.getUniformResample().apply(points);
    // resampleResult.getParameters();
    int sum = resampleResult.count(); // usually around 430
    if (ResampledLidarRender.MIN_POINTS < sum) {
      {
        TestCase.assertTrue(400 < sum);
        SlamScore slamScore = ImageScore.of(map_image);
        GeometricLayer geometricLayer = new GeometricLayer(ViewLcmFrame.MODEL2PIXEL_INITIAL, Array.zeros(3));
        geometricLayer.pushMatrix(model);
        geometricLayer.pushMatrix(LIDAR);
        Stopwatch stopwatch = Stopwatch.started();
        // System.out.println("---");
        SlamResult slamResult = SpinDunk.of(DubendorfSlam.SE2MULTIRESGRIDS, geometricLayer, resampleResult, slamScore);
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
        // System.out.println(duration + "[s]");
        TestCase.assertTrue(duration < 15.3); // TODO reduce
        Scalar ratio = N.DOUBLE.apply(slamResult.getMatchRatio());
        Clip.unit().requireInside(ratio);
        int quality = slamResult.getMatchRatio().multiply(RealScalar.of(sum * 255)).number().intValue();
        // System.out.println(quality);
        // TestCase.assertTrue(40000 < quality);
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
        Scalar rate = RealScalar.ZERO; // dstate.Get(2); // FIXME
        for (Tensor scattered : resampleResult.getPointsSpin(rate))
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
          ImageIO.write(image, "png", UserHome.Pictures("spin" + count + ".png"));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      ++count;
    } else
      ++skipped;
  }
}