// code by jph
package ch.ethz.idsc.retina.alg.slam;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;

/** simultaneous localization and mapping */
public class SlamEvent {
  public BufferedImage bufferedImage;
  public Tensor global_pose;
}
