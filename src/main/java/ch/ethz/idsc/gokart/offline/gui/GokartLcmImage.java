// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.Timing;

public enum GokartLcmImage {
  ;
  static final int FX = 8;

  /** @param gokartLogFileIndexer
   * @return image indicating */
  public static BufferedImage of(GokartLogFileIndexer gokartLogFileIndexer) {
    Timing timing = Timing.started();
    Tensor tensor = Tensors.empty();
    for (GokartLogImageRow gokartLogImageRow : gokartLogFileIndexer.gokartLogImageRows) {
      Tensor stact = Transpose.of(gokartLogImageRow.tensor());
      System.out.println("stact=" + Dimensions.of(stact));
      tensor.append(ImageResize.nearest(stact.map(gokartLogImageRow.getColorDataGradient()), FX, 1));
    }
    BufferedImage bufferedImage = ImageFormat.of(Flatten.of(tensor, 1));
    System.out.println("image gen: " + timing.seconds());
    return bufferedImage;
  }
}
