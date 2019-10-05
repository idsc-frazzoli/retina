// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.ImageFormat;

/** image from log file */
public enum GokartLcmImage {
  ;
  static final int FX = 8;

  /** @param gokartLogFileIndexer
   * @return image indicating */
  public static BufferedImage of(GokartLogFileIndexer gokartLogFileIndexer) {
    Tensor tensor = Tensors.empty();
    for (GokartLogImageRow gokartLogImageRow : gokartLogFileIndexer.gokartLogImageRows) {
      try {
        Tensor row = Transpose.of(gokartLogImageRow.tensor());
        tensor.append(ImageResize.nearest(row.map(gokartLogImageRow.getColorDataGradient()), FX, 1));
      } catch (Exception exception) {
        System.err.println(gokartLogImageRow.getClass().getSimpleName());
      }
    }
    return ImageFormat.of(Flatten.of(tensor, 1));
  }
}
