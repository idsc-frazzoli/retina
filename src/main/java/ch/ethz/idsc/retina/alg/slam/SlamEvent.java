// code by jph
package ch.ethz.idsc.retina.alg.slam;

import java.awt.image.BufferedImage;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;

/** simultaneous localization and mapping */
public class SlamEvent {
  public Tensor global_pose;
  public Tensor move;
  public BufferedImage bufferedImage;
  public List<Tensor> pose_lidar;
}
