// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.nio.FloatBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlListener;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** localization that uses lidar in combination with gyro rate to rectify measurements
 * 
 * https://github.com/idsc-frazzoli/retina/files/1801718/20180221_2nd_gen_localization.pdf */
public class ScatterImageInvoke implements LidarRayBlockListener, GokartPoseListener, ManualControlListener {
  private static final int MIN_POINTS = LocalizationConfig.GLOBAL.min_points.number().intValue();
  // ---
  /** 3x3 transformation matrix of lidar to center of rear axle */
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart();
  private final ScatterImage scatterImage;
  private GokartPoseEvent gokartPoseEvent = null;
  private boolean status;

  /** @param map_image
   * @param pose {x[m], y[m], angle}
   * @param scatterImage */
  public ScatterImageInvoke(ScatterImage scatterImage) {
    this.scatterImage = scatterImage;
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (Objects.nonNull(gokartPoseEvent)) {
      FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
      Tensor points = Tensors.vector(i -> Tensors.of( //
          DoubleScalar.of(floatBuffer.get()), //
          DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
      // Scalar rate = gokartPoseEvent.getGyroZ().divide(SensorsConfig.GLOBAL.vlp16_rate);
      // List<Tensor> list = LocalizationConfig.GLOBAL.getResample() //
      // .apply(points).getPointsSpin(SensorsConfig.GLOBAL.vlp16_relativeZero, rate);
      // Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
      // int sum = scattered.length(); // usually around 430
      // if (MIN_POINTS < sum)
      {
        if (status) {
          System.out.println("render");
          Tensor model = PoseHelper.toSE2Matrix(gokartPoseEvent.getPose());
          scatterImage.render(model.dot(lidar), points);
          // scattered);
        }
      }
      // else {
      // System.err.println("few points " + sum);
      // }
    }
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent getEvent) {
    gokartPoseEvent = getEvent;
  }

  @Override
  public void manualControl(ManualControlInterface manualControlInterface) {
    status = manualControlInterface.isAutonomousPressed();
  }
}