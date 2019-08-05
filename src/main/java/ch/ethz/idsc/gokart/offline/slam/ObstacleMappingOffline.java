// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.track.TrackReconConfig;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.app.map.ErodableMap;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ObstacleMappingOffline extends LidarProcessOffline {
  private final SpacialXZObstaclePredicate spacialXZObstaclePredicate = //
      TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final ErodableMap erodableMap;
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  int count = 0;

  public ObstacleMappingOffline() {
    super(new Vlp16SegmentProvider(SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue(), -4));
    BufferedImage bufferedImage = new BufferedImage(160, 80, BufferedImage.TYPE_BYTE_GRAY);
    Tensor model2pixel = Dot.of( //
        Se2Matrix.of(Tensors.vector(32, 20, Math.PI / 4)), //
        DiagonalMatrix.of( //
            38.4 / bufferedImage.getWidth(), //
            19.2 / bufferedImage.getHeight(), 1), //
        Se2Matrix.flipY(bufferedImage.getHeight()));
    erodableMap = new ErodableMap(bufferedImage, model2pixel);
  }

  @Override
  protected void protected_event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      // TODO lidar offset missing
      erodableMap.setReference(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
    }
    if (Scalars.lessThan(time_next, time)) {
      File folder = HomeDirectory.Pictures("eroded");
      folder.mkdir();
      try {
        ImageIO.write(erodableMap.erodedRegion(3).bufferedImage(), "png", new File(folder, String.format("%06d.png", count)));
        System.out.println("written " + count);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      ++count;
      time_next = time.add(Quantity.of(1, SI.SECOND));
    }
  }

  @Override
  protected void process(float x, float y, float z) {
    boolean isObstacle = spacialXZObstaclePredicate.isObstacle(x, z);
    // ---
    erodableMap.setPixel(x, y, isObstacle);
  }
}
