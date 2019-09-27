// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
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
      try {
        Tensor stact = Transpose.of(gokartLogImageRow.tensor());
        Tensor nearest = ImageResize.nearest(stact.map(gokartLogImageRow.getColorDataGradient()), FX, 1);
        tensor.append(nearest);
      } catch (Exception exception) {
        System.err.println(gokartLogImageRow.getClass().getSimpleName());
      }
    }
    BufferedImage bufferedImage = ImageFormat.of(Flatten.of(tensor, 1));
    System.out.println("image gen: " + timing.seconds());
    return bufferedImage;
  }
}
