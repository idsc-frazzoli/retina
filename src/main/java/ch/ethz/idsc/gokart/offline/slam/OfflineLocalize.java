// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.slam.SlamScore;
import ch.ethz.idsc.gokart.gui.top.ImageScore;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Clip;

/** functionality is strictly for offline processing
 * do not use during live operation: memory consumption is not bounded */
public abstract class OfflineLocalize implements LidarRayBlockListener, DavisImuFrameListener {
  private static final Scalar ZERO_RATE = Quantity.of(0, SI.PER_SECOND);
  // ---
  protected final SlamScore slamScore;
  private final List<LocalizationResultListener> listeners = new LinkedList<>();
  public final Tensor skipped = Tensors.empty();
  /** 3x3 matrix */
  private Tensor gyro_y = Tensors.empty();
  protected Tensor model;
  private Scalar time;

  /** @param map_image
   * @param pose {x[m], y[m], angle} */
  public OfflineLocalize(BufferedImage map_image, Tensor pose) {
    this.model = GokartPoseHelper.toSE2Matrix(pose);
    // ---
    if (map_image.getType() != BufferedImage.TYPE_BYTE_GRAY)
      throw new RuntimeException();
    slamScore = ImageScore.of(map_image);
  }

  public final void addListener(LocalizationResultListener localizationResultListener) {
    listeners.add(localizationResultListener);
  }

  public final void setTime(Scalar time) {
    this.time = time;
  }

  public final Tensor getPositionVector() {
    return Se2Utils.fromSE2Matrix(model);
  }

  @Override // from DavisImuFrameListener
  public void imuFrame(DavisImuFrame davisImuFrame) {
    Scalar rate = SensorsConfig.GLOBAL.getGyroZ(davisImuFrame);
    gyro_y.append(rate);
  }

  protected final Scalar getGyroAndReset() {
    Scalar mean = Tensors.isEmpty(gyro_y) //
        ? ZERO_RATE
        : Mean.of(gyro_y).Get();
    gyro_y = Tensors.empty();
    // TODO use imu tracker instead
    // System.out.println("gyro " + mean);
    return mean;
  }

  protected final void appendRow(Scalar ratio, int sum, double duration) {
    LocalizationResult localizationResult = new LocalizationResult( //
        time, Se2Utils.fromSE2Matrix(model), Clip.unit().requireInside(ratio));
    listeners.forEach(listener -> listener.localizationCallback(localizationResult));
  }

  protected final void skip() {
    skipped.append(time);
    System.err.println("skip " + time);
  }
}
