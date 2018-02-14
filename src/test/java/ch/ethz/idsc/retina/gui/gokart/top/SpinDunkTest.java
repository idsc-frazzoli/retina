// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.dev.zhkart.pos.LocalizationConfig;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.ResampleResult;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

/** the test matches 3 consecutive lidar scans to the dubendorf hangar map
 * the matching qualities are
 * 51255
 * 43605
 * 44115 */
class SpinLidarRayBlockListener implements LidarRayBlockListener {
  static final boolean EXPORT = true;
  // ---
  BufferedImage map_image = StoreMapUtil.loadOrNull();
  int skipped = 0;
  int count = 0;
  Tensor model2pixel = Tensors.matrixDouble(new double[][] { //
      { -6.77422, 3.21868, 422.04915 }, //
      { +3.21868, 6.77422, 213.03233 }, //
      { 0, 0, 1 } });

  public SpinLidarRayBlockListener() {
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
      TestCase.assertTrue(400 < sum);
      SlamScore slamScore = ImageScore.of(map_image);
      GeometricLayer glmap = new GeometricLayer(model2pixel, Array.zeros(3));
      Stopwatch stopwatch = Stopwatch.started();
      // System.out.println("---");
      SlamResult slamResult = SpinDunk.of(DubendorfSlam.SE2MULTIRESGRIDS, glmap, resampleResult, slamScore);
      double duration = stopwatch.display_seconds(); // typical is 0.03
      Tensor delta = slamResult.getTransform();
      Tensor dstate = Se2Utils.fromSE2Matrix(delta);
      // System.out.println(dstate.map(Round._6));
      model2pixel = model2pixel.dot(delta); // advance gokart
      // System.out.println(Pretty.of(delta.map(Round._4)));
      if (count == 0)
        TestCase.assertEquals(delta, IdentityMatrix.of(3));
      else {
        TestCase.assertFalse(delta.equals(IdentityMatrix.of(3)));
        Clip.function(-0.04, 0.01).requireInside(dstate.Get(2));
      }
      TestCase.assertEquals(Dimensions.of(delta), Arrays.asList(3, 3));
      // System.out.println(duration + "[s]");
      TestCase.assertTrue(duration < 15.3); // TODO reduce
      Scalar ratio = N.DOUBLE.apply(slamResult.getMatchRatio());
      Clip.unit().requireInside(ratio);
      int quality = slamResult.getMatchRatio().multiply(RealScalar.of(sum * 255)).number().intValue();
      // System.out.println(quality);
      TestCase.assertTrue(45000 < quality);
      // ---
      if (EXPORT) {
        BufferedImage image = new BufferedImage(map_image.getWidth(), map_image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = image.createGraphics();
        graphics2d.drawImage(map_image, 0, 0, null);
        GeometricLayer geometricLayer = new GeometricLayer(model2pixel, Array.zeros(3));
        graphics2d.setColor(Color.GREEN);
        Scalar rate = dstate.Get(2);
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
        graphics2d.drawString("q=" + quality, 0, 10);
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

public class SpinDunkTest extends TestCase {
  public void testSimple() throws Exception {
    // global pose is approximately {56.137[m], 57.022[m], -1.09428}
    // new state={56.18474711501799[m], 57.042752703987055[m], -1.0956022539443036}
    File file = new File("src/test/resources/localization", "vlp16.center.ray_autobox.rimo.get.lcm");
    assertTrue(file.isFile());
    // ---
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.vlp16_p01deg();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    SpinLidarRayBlockListener lidarRayBlockListener = new SpinLidarRayBlockListener();
    lidarAngularFiringCollector.addListener(lidarRayBlockListener);
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
        if (channel.equals("vlp16.center.ray"))
          velodyneDecoder.lasers(byteBuffer);
      }
    };
    OfflineLogPlayer.process(file, offlineLogListener);
    assertEquals(lidarRayBlockListener.skipped, 1);
  }
}
