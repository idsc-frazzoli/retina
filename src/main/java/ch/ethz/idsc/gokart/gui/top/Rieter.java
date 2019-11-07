// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

public enum Rieter {
  ;
  private static final Tensor _20191104 = Tensors.matrix(new Number[][] { //
      { 7.5, +0.0, 0.0 }, //
      { 0.0, -7.5, 540 }, //
      { 0.0, +0.0, 1.0 }, //
  }).unmodifiable();

  public static BackgroundImage backgroundImage20191104() {
    BufferedImage bufferedImage = ResourceData.bufferedImage("/rieter/localization/20191022.png");
    Tensor tensor = ImageFormat.from(bufferedImage);
    tensor = tensor.map(s -> RealScalar.of(255).subtract(s.multiply(RationalScalar.HALF)));
    return new BackgroundImage(ImageFormat.of(tensor), _20191104);
  }
}
