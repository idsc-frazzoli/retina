// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
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

class CountLidarRayBlockListener implements LidarRayBlockListener {
  BufferedImage map_image = StoreMapUtil.loadOrNull();
  int skipped = 0;
  int count = 0;

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    TestCase.assertFalse(floatBuffer.hasRemaining());
    List<Tensor> list = LocalizationConfig.GLOBAL.getUniformResample().apply(points).getPoints();
    Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
    int sum = scattered.length(); // usually around 430
    if (ResampledLidarRender.MIN_POINTS < sum) {
      TestCase.assertTrue(400 < sum);
      Tensor model2pixel = Tensors.matrixDouble(new double[][] { //
          { -6.77422, 3.21868, 422.04915 }, //
          { +3.21868, 6.77422, 213.03233 }, //
          { 0, 0, 1 } });
      SlamScore slamScore = ImageScore.of(map_image);
      GeometricLayer glmap = new GeometricLayer(model2pixel, Array.zeros(3));
      Stopwatch stopwatch = Stopwatch.started();
      SlamResult slamResult = SlamDunk.of(DubendorfSlam.SE2MULTIRESGRIDS, glmap, scattered, slamScore);
      double duration = stopwatch.display_seconds(); // typical is 0.03
      Tensor delta = slamResult.getTransform();
      if (count == 0)
        TestCase.assertEquals(delta, IdentityMatrix.of(3));
      else
        TestCase.assertFalse(delta.equals(IdentityMatrix.of(3)));
      TestCase.assertEquals(Dimensions.of(delta), Arrays.asList(3, 3));
      // System.out.println(duration + "[s]");
      TestCase.assertTrue(duration < 0.3);
      Scalar ratio = N.DOUBLE.apply(slamResult.getMatchRatio());
      Clip.unit().isInsideElseThrow(ratio);
      int quality = slamResult.getMatchRatio().multiply(RealScalar.of(sum * 255)).number().intValue();
      // System.out.println(quality);
      TestCase.assertTrue(20000 < quality);
      ++count;
    } else
      ++skipped;
  }
}

public class SlamDunkTest extends TestCase {
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
    CountLidarRayBlockListener lidarRayBlockListener = new CountLidarRayBlockListener();
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
